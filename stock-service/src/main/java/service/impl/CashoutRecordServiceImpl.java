package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.Constant;
import entity.CashoutRecord;
import entity.UserBankInfo;
import entity.UserInfo;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CashoutTypeEnum;
import enums.CurrencyEnum;
import mapper.CashoutRecordMapper;
import service.CashoutRecordService;
import service.IpAddressService;
import service.SysParamConfigService;
import service.UserBankInfoService;
import service.UserInfoService;
import utils.OrderNumberGenerator;
import utils.PasswordGenerator;
import utils.StringUtil;
import vo.manager.CashoutRecordListParamVO;
import vo.manager.CashoutRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

/**
 * <p>
 * 提现记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-11-20
 */
@Service
public class CashoutRecordServiceImpl extends ServiceImpl<CashoutRecordMapper, CashoutRecord> implements CashoutRecordService {

	@Resource
	private CashoutRecordMapper cashoutRecordMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserBankInfoService userBankInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Override
	public Response<CashoutRecordListVO> managerList(CashoutRecordListParamVO param) {
		CashoutRecordListVO vo = new CashoutRecordListVO();
		Page<CashoutRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		cashoutRecordMapper.managerList(page, param);
		QueryWrapper<CashoutRecord> qw = new QueryWrapper<>();
		qw.select("ifnull(sum(order_amount),0) as orderAmount, ifnull(sum(final_amount),0) as finalAmount");
		Map<String, Object> map = this.getMap(qw);
		vo.setTotalOrderAmount((BigDecimal) map.get("orderAmount"));
		vo.setTotalFinalAmount((BigDecimal) map.get("finalAmount"));
		vo.setPage(page);
		return Response.successData(vo);
	}

	@Override
	@Transactional
	public Response<Void> submit(Integer userId, BigDecimal amount, String fundPwd, String ip) {
		UserInfo ui = userInfoService.getById(userId);
		if(ui == null) {
			return Response.fail("用户信息错误");
		}
		UserBankInfo ubi = userBankInfoService.lambdaQuery().eq(UserBankInfo::getUserId, userId).one();
		if(ubi == null) {
			return Response.fail("您还未绑定银行卡，请先绑定");
		}
		if (StringUtil.isEmpty(ui.getFundPwd())) {
			return Response.fail("交易密码未设置");
		}
		fundPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, fundPwd);
		if(!ui.getFundPwd().equals(fundPwd)) {
			return Response.fail("交易密码错误");
		}
		Integer count = this.lambdaQuery().eq(CashoutRecord::getOrderStatus, 0).eq(CashoutRecord::getUserId, userId).count();
		if (count > 0) {
			return Response.fail("转出申请尚未审核，请勿重复申请");
		}
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		String startWithdrawalTime = spc.getStartWithdrawalTime();
		String endWithdrawalTime = spc.getEndWithdrawalTime();
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
		Timestamp timestamp = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
		Date date = new Date(timestamp.getTime());
		SimpleDateFormat yyyyMMddDf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat yyyyMMddHHmmDf = new SimpleDateFormat("yyyyMMddHH:mm");
        Date startTime = null;
        Date endTime = null;
        String dateFmt = yyyyMMddDf.format(date);
        try {
        	startTime =	yyyyMMddHHmmDf.parse(dateFmt + startWithdrawalTime);
        	endTime = 	yyyyMMddHHmmDf.parse(dateFmt + endWithdrawalTime);
        } catch(Exception ex) {
        	
        }
        if(date.compareTo(startTime) == -1 || date.compareTo(endTime) == 1) {
        	return Response.fail("提现时间在" + startWithdrawalTime + "至" + endWithdrawalTime);
        }
        if(spc.getMinCashoutAmount() != null && spc.getMinCashoutAmount().compareTo(BigDecimal.ZERO) == 1) {
        	if(amount.compareTo(spc.getMinCashoutAmount()) == -1) {
        		return Response.fail("最小提现金额为" + spc.getMinCashoutAmount() + "¥");
        	}
        }
        BigDecimal fee = amount.multiply(spc.getCashoutFeeRate()).setScale(2, RoundingMode.HALF_UP);
        if(fee.compareTo(spc.getCashoutFee()) == -1) {
        	fee = spc.getCashoutFee();
        }
        //判断用户可用金额小于提现金额，不允许提现操作
		if (ui.getAvailableAmt().compareTo(amount) == -1) {
			return Response.fail("可用金额不足！");
		} 
		//创建提现记录
		CashoutRecord cr = new CashoutRecord();
		cr.setOrderSn(OrderNumberGenerator.create(3));
		cr.setUserId(userId);
		cr.setRealName(ubi.getRealName());
		cr.setCardNo(ubi.getCardNo());
		cr.setBankName(ubi.getBankName());
		cr.setBranchAddress(ubi.getBranchBankAddress());
		cr.setOrderAmount(amount);
		cr.setFee(fee);
		cr.setFinalAmount(BigDecimal.ZERO);
		cr.setCashoutTypeCode(CashoutTypeEnum.Bank.getCode());
		cr.setCashoutTypeName(CashoutTypeEnum.Bank.getName());
		cr.setRequestTime(date);
		cr.insert();
		//执行提现操作
		userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.BankCardWithdrawalApplication, amount, "", CurrencyEnum.CNY, BigDecimal.ONE, ip, ipAddressService.getIpAddress(ip).getAddress2(), ui.getOperator());
		return Response.success();
	}

	@Override
	public void record(Page<CashoutRecordVO> page, CashinAndOutRecordParamVO param) {
		cashoutRecordMapper.record(page, param);
	}

}
