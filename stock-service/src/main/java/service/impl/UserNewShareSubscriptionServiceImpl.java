package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.Constant;
import entity.NewShare;
import entity.StockDataChangeRecord;
import entity.StockInfo;
import entity.UserFinancingInterestInfo;
import entity.UserInfo;
import entity.UserNewShareSubscription;
import entity.UserNewShareSubscriptionOperateLog;
import entity.UserPositionChangeRecord;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockDataChangeTypeEnum;
import enums.StockPendingStatusEnum;
import enums.StockTypeEnum;
import enums.UserStockPositionChangeTypeEnum;
import mapper.NewShareMapper;
import mapper.UserNewShareSubscriptionMapper;
import service.IpAddressService;
import service.NewShareService;
import service.SiteInternalMessageService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFinancingInterestInfoService;
import service.UserInfoService;
import service.UserNewShareSubscriptionService;
import service.UserStockPositionService;
import utils.OrderNumberGenerator;
import vo.manager.NewShareSubscriptionListVO;
import vo.manager.NewShareSubscriptionSearchParamVO;
import vo.server.NewShareSubscripWithCashParamVO;
import vo.server.NewShareSubscripWithFinancingParamVO;
import vo.server.NewShareSubscripWithZeroParamVO;
import vo.server.UserNewShareSubscriptionDetailVO;
import vo.server.UserNewShareSubscriptionListVO;

/**
 * <p>
 * 新股申购记录表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-11-21
 */
@Service
public class UserNewShareSubscriptionServiceImpl
		extends ServiceImpl<UserNewShareSubscriptionMapper, UserNewShareSubscription>
		implements UserNewShareSubscriptionService {

	@Resource
	private UserNewShareSubscriptionMapper userNewShareSubscriptionMapper;
	
	@Resource
	private NewShareMapper newShareMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private NewShareService newShareService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private SiteInternalMessageService siteInternalMessageService;
	
	@Resource
	UserFinancingInterestInfoService userFinancingInterestInfoService;

	@Override
	public void managerList(Page<NewShareSubscriptionListVO> page, NewShareSubscriptionSearchParamVO param) {
		userNewShareSubscriptionMapper.managerList(page, param);
		List<NewShareSubscriptionListVO> list = page.getRecords();
		if(list.size() > 0) {
			BigDecimal usdRate = null, hkdRate = null;
			for(NewShareSubscriptionListVO i : list) {
				if(param.getSubscriptionType() == 2) {
					try {
						BigDecimal winNums = i.getSubscriptionAmount().multiply(new BigDecimal(i.getLever())).divide(i.getBuyingPrice(), 0, RoundingMode.DOWN);
						i.setRemark("用户id：" + i.getUserId() + "\n申购金额：" + i.getSubscriptionAmount() + "\t杠杆倍数：" + i.getLever() + "\n最大可中签数量：" + winNums);
					} catch (Exception e) {
						i.setRemark("（数据错误）用户id：" + i.getUserId() + "\n申购金额：" + i.getSubscriptionAmount() + "\t杠杆倍数：" + i.getLever() + "\n最大可中签数量无法计算");
					}
				} else if(param.getSubscriptionType() == 1) {
					i.setRemark("用户id：" + i.getUserId() + "\n申购金额：" + i.getSubscriptionAmount() + "\n最大可中签数量：" + i.getPurchaseQuantity());
				} else {
					i.setRemark("用户id：" + i.getUserId() + "\n可用金额：" + i.getAvailableAmt() + "\t最大可中签数量：");
					switch(i.getStockType()) {
					case "us":
						if(usdRate == null) {
							usdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.USD);
						}
						i.setExchangeRate(usdRate);
						i.setRemark(i.getRemark() + i.getAvailableAmt().multiply(usdRate).divide(i.getBuyingPrice(), 0, RoundingMode.DOWN));
						break;
					case "hk":
						if(hkdRate == null) {
							hkdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.HKD);
						}
						i.setExchangeRate(hkdRate);
						i.setRemark(i.getRemark() + i.getAvailableAmt().multiply(hkdRate).divide(i.getBuyingPrice(), 0, RoundingMode.DOWN));
						break;
					default:
						i.setRemark(i.getRemark() + i.getAvailableAmt().divide(i.getBuyingPrice(), 0, RoundingMode.DOWN));
						break;
					}
				}
			}
		}
	}

	@Override
	@Transactional
	public Response<Void> managerWin(Integer id, Integer awardQuantity, String ip, String operator) {
		if(awardQuantity <= 0) {
			return Response.fail("请输入正确的数量");
		}
		UserNewShareSubscription uns = this.getById(id);
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 0) {//申购状态,当为-非申购中-处理修改申购签数
			//修改中签数量
			if(uns.getSubscriptionStatus() == 1) {//申购状态，当为-已中签-处理修改签数逻辑
				if(uns.getAwardQuantity().equals(awardQuantity)) {
					return Response.fail("新输入不同的数量");
				}
				this.lambdaUpdate().eq(UserNewShareSubscription::getId, id).set(UserNewShareSubscription::getAwardQuantity, awardQuantity).update();
				//发送站内信
				siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股中签数量变更",operator,"您申购的新股【" + uns.getStockName() + "（" + uns.getStockCode() + "）】中签数量变更为" + awardQuantity + "，请及时关注哦。");				
				return Response.success();
			}
			return Response.fail("发布中签失败，记录状态不是申购中");
		}
		NewShare ns = newShareService.getById(uns.getNewShareId());
		if(ns == null) {
			return Response.fail("新股信息不存在");
		}
		if(ns.getPaymentDeadline() == null) {
			return Response.fail("请先设置认缴截止时间");
		}
		Date now = new Date();
		//融资申购认缴
		switch(uns.getSubscriptionType()) {
		default:
			this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
			.set(UserNewShareSubscription::getAwardTime, now)
			.set(UserNewShareSubscription::getAwardQuantity, awardQuantity)
			.set(UserNewShareSubscription::getSubscriptionStatus, 1).update();
			break;
		case 1:
		case 2:
			BigDecimal lever = new BigDecimal(uns.getLever());
			BigDecimal finalOrderAmount = uns.getBuyingPrice().multiply(new BigDecimal(awardQuantity));
			BigDecimal orderAmount = uns.getSubscriptionAmount().multiply(lever);
			StringBuilder deSummary = new StringBuilder("新股中签");
			deSummary.append(uns.getStockName()).append("(").append(uns.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(uns.getStockType())).append(")，申购价格：");
			deSummary.append(uns.getBuyingPrice()).append("，中签数量：").append(uns.getAwardQuantity()).append("，申购记录id：" + id);
			CurrencyEnum currency = CurrencyEnum.getByStockType(uns.getStockType());
			String ipAddress = this.ipAddressService.getIpAddress(ip).getAddress2();
			this.userInfoService.updateUserAvailableAmt(uns.getUserId(), AmtDeTypeEnum.WinNewShare, uns.getSubscriptionAmount(), deSummary.toString(), currency, uns.getExchangeRate(), ip, ipAddress, operator);
			BigDecimal actualSubscriptionAmount = finalOrderAmount.divide(lever).setScale(2, RoundingMode.HALF_UP);
			if(orderAmount.compareTo(finalOrderAmount) == 1) {
				BigDecimal difference = uns.getSubscriptionAmount().subtract(actualSubscriptionAmount);
				deSummary = new StringBuilder("申购新股金额回退，申购记录id：").append(uns.getId());
				this.userInfoService.updateUserAvailableAmt(uns.getUserId(), AmtDeTypeEnum.ReturnFromNewShare, difference, deSummary.toString(), currency, uns.getExchangeRate(), ip, ipAddress, operator);
			}
			this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
			.set(UserNewShareSubscription::getAwardTime, now)
			.set(UserNewShareSubscription::getPaymentTime, now)
			.set(UserNewShareSubscription::getAwardQuantity, awardQuantity)
			.set(UserNewShareSubscription::getSubscriptionStatus, 2).update();
			break;
		}
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(1);
		unsol.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		unsol.setIp(ip);
		unsol.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股中签",operator,"恭喜您，新股申购【" + uns.getStockName() + "（" + uns.getStockCode() + "）】中签成功，中签数量：" + awardQuantity + "，请及时关注哦。");				
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<Void> managerFail(Integer id, String ip, String operator) {
		UserNewShareSubscription uns = this.getById(id);
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 0) {
			return Response.fail("发布中签失败，记录状态不是申购中");
		}
		this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
		.set(UserNewShareSubscription::getAwardQuantity, 0)
		.set(UserNewShareSubscription::getSubscriptionStatus, 4).update();
		String ipAddress = ipAddressService.getIpAddress(ip).getAddress2();
		if(uns.getSubscriptionType() != 0) {
			this.userInfoService.updateUserAvailableAmt(uns.getUserId(), 
					AmtDeTypeEnum.SubscripNewShareFail, 
					uns.getSubscriptionAmount(), 
					"新股申购未中签，申购记录id：" + id, 
					CurrencyEnum.getByStockType(uns.getStockType()), 
					uns.getExchangeRate(), ip, ipAddress, operator);
		}
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(4);
		unsol.setIpAddress(ipAddress);
		unsol.setIp(ip);
		unsol.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股申购未中签",operator,"很遗憾，您申购【" + uns.getStockName() + "（" + uns.getStockCode() + "）】本次未中签。");				
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<Void> managerPay(Integer id, String ip, String operator) {
		UserNewShareSubscription uns = this.getById(id);
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 1) {
			return Response.fail("认缴失败，记录状态不是已中签");
		}
		Date now = new Date();
		NewShare newShare = this.newShareMapper.selectById(uns.getNewShareId());
		if(newShare == null) {
			return Response.fail("认缴失败，新股数据不存在");
		}
		if(newShare.getPaymentDeadline() == null) {
			return Response.fail("请先设置认缴截止时间");
		}
		if(now.compareTo(newShare.getPaymentDeadline()) == 1) {
			return Response.fail("缴纳失败，此产品的认缴截止日期为 " + new SimpleDateFormat("yyyy/MM/dd").format(newShare.getSubscriptionDeadline()));
		}
		CurrencyEnum currency = CurrencyEnum.getByStockType(uns.getStockType());
		BigDecimal exchangeRate = uns.getExchangeRate();
		String address = this.ipAddressService.getIpAddress(ip).getAddress2();
		BigDecimal	orderAmount = uns.getBuyingPrice().multiply(new BigDecimal(uns.getAwardQuantity())); 
		UserInfo ui = this.userInfoService.getById(uns.getUserId());
		BigDecimal availableAmt = ui.getAvailableAmt().multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
		if(availableAmt.compareTo(orderAmount) == -1) {
			return Response.fail("认缴失败，用户现金余额不足，当前现金余额：" + availableAmt + currency.getName());
		}
		StringBuilder deSummary = new StringBuilder();
		deSummary.append("认缴新股：").append(newShare.getStockName()).append("(").append(newShare.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(uns.getStockType())).append(")，申购价格：");
		deSummary.append(uns.getBuyingPrice()).append("，中签数量：").append(uns.getAwardQuantity()).append("，申购记录id：" + id);
		this.userInfoService.updateUserAvailableAmt(uns.getUserId(), AmtDeTypeEnum.PayNewShare, orderAmount, deSummary.toString(), currency, exchangeRate, ip, address, operator);
		this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
			.set(UserNewShareSubscription::getSubscriptionStatus, 2)
			.set(UserNewShareSubscription::getPaymentTime, now).update();
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(2);
		unsol.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		unsol.setIp(ip);
		unsol.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股已认缴",operator,"恭喜您，您申购的新股【" + uns.getStockName() + "（" + uns.getStockCode() + "）】已确认缴纳，等待工作人员审核，请及时关注持仓信息。");				
		return Response.success();
	}
	
	/**
	 * 新股转持仓
	 */
	@Override
	@Transactional
	public Response<Void> managerTransfer(Integer id, String ip, String operator) {
		UserNewShareSubscription uns = this.getById(id);
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 2) {
			return Response.fail("转入持仓失败，申购记录不是已认缴状态");
		}
		Date now = new Date();
		NewShare newShare = this.newShareService.getById(uns.getNewShareId());
		if(newShare == null) {
			return Response.fail("转入持仓失败，新股信息不存在");
		}
		if(newShare.getListingDate() == null) {
			//新股上市为null时，修改新股上市时间为当前时间
			NewShare updateNewShare = new NewShare().setId(id).setListingDate(now);
			updateNewShare.updateById();
		} else if(newShare.getListingDate().compareTo(now) == 1) {
			return Response.fail("应当提示，新股未上市，禁止转入持仓");
		}
		String ipAddress = ipAddressService.getIpAddress(ip).getAddress2();
		//1、变更用户新股申购状态
		this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
				.set(UserNewShareSubscription::getSubscriptionStatus, 3)
				.set(UserNewShareSubscription::getTransferTime, now).update();
		//2、增加用户新股变更日志
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(3);
		unsol.setIpAddress(ipAddress);
		unsol.setIp(ip);
		unsol.insert();
		//3、查询当前股票信息
		StockInfo si = this.stockInfoService.lambdaQuery().eq(StockInfo::getStockCode, uns.getStockCode()).eq(StockInfo::getStockType, uns.getStockType()).one();
		if(si == null) {//系统无当前股票数据时，添加一条股票数据与操作记录
			si = new StockInfo();
			si.setStockCode(uns.getStockCode());
			si.setStockName(uns.getStockName());
			si.setStockType(uns.getStockType());
			if(uns.getStockCode().startsWith("688") && uns.getStockCode().equals("sh")) {
				si.setStockPlate("科创");
			} else if(uns.getStockCode().equals("sz") && uns.getStockCode().startsWith("30")) {
				si.setStockPlate("创业");
			}
			si.setIsShow(true);
			si.setIsLock(false);
			si.setCreator("系统");
			si.insert();
			StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
			stockDataChangeRecord.setStockId(si.getId());
			stockDataChangeRecord.setStockCode(si.getStockCode());
			stockDataChangeRecord.setStockName(si.getStockName());
			stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
			stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
			stockDataChangeRecord.setNewContent(si.toString());
			stockDataChangeRecord.setIp("127.0.0.1");
			stockDataChangeRecord.setIpAddress("服务器本机ip");
			stockDataChangeRecord.setOperator("系统");
			stockDataChangeRecord.insert();
		} 
		//4、合并/增加-持仓数据，（合并的条件【当前用户、当前股票类型、当前股票代码、当前杠杆倍数、不是大宗交易、做多、已成交】）
		UserStockPosition newUsp = userStockPositionService.lambdaQuery()
				.eq(UserStockPosition::getUserId, uns.getUserId())//当前-用户
				.eq(UserStockPosition::getStockType, uns.getStockType())//当前-股票类型
				.eq(UserStockPosition::getStockCode, uns.getStockCode())//当前-股票代码
				.eq(UserStockPosition::getIsBlockTrading, false)//不是大宗交易
				.eq(UserStockPosition::getPositionDirection, Constant.GO_LONG)//做多
				.eq(UserStockPosition::getPositionStatus, 2)//已成交					
				.eq(UserStockPosition::getLever, uns.getLever())//当前-杠杆倍数
				.one();
		if(newUsp != null) {//合并持仓数据
			//股数
			BigDecimal totalShares = new BigDecimal(newUsp.getBuyingShares() + uns.getAwardQuantity());
			//总持仓金额
			BigDecimal totalMarketValue = newUsp.getBuyingPrice().multiply(new BigDecimal(newUsp.getBuyingShares()));
			totalMarketValue = totalMarketValue.add(uns.getBuyingPrice().multiply(new BigDecimal(uns.getAwardQuantity())));
			//买入价格重新计算（-为总价格/总股数）
			BigDecimal holdPrice = totalMarketValue.divide(totalShares, 3, RoundingMode.DOWN);
			//修改持仓记录的买入价格、买入股数
			this.userStockPositionService.lambdaUpdate()
					.eq(UserStockPosition::getId, newUsp.getId())
					.set(UserStockPosition::getBuyingPrice, holdPrice)
					.set(UserStockPosition::getBuyingShares, totalShares)
					.update();
		} else {//无可合并持仓数据时，增加持仓数据
			StockParamConfig spc = this.sysParamConfigService.getSysParamConfig();
			newUsp = new UserStockPosition();
			newUsp.setPositionType(Constant.ACCOUNT_TYPE_REAL);
			newUsp.setUserId(uns.getUserId());
			newUsp.setStockName(uns.getStockName());
			newUsp.setStockCode(uns.getStockCode());
			newUsp.setStockType(uns.getStockType());
			newUsp.setStockPlate(si.getStockPlate());
			newUsp.setPositionTime(now);
			newUsp.setPositionStatus(StockPendingStatusEnum.COMPLETED.getCode());
			newUsp.setBuyingPrice(uns.getBuyingPrice());
			newUsp.setPositionDirection(Constant.GO_LONG);
			newUsp.setBuyingShares(uns.getAwardQuantity());
			newUsp.setLever(uns.getLever());
			newUsp.setBuyingFee(BigDecimal.ZERO);//手续费0
			newUsp.setBuyingStampDuty(BigDecimal.ZERO);//印花税0
			newUsp.setIsLock(false);
			newUsp.setIsBlockTrading(false);
			Integer lockInPeriod = si.getLockInPeriod();//锁仓天数
			if(lockInPeriod == null || lockInPeriod == 0) {
				switch(si.getStockType()) {
				case "us":
					lockInPeriod = spc.getMarketUsLockInPeriod();
					break;
				case "hk":
					lockInPeriod = spc.getMarketHkLockInPeriod();
					break;
				default :
					lockInPeriod = spc.getMarketALockInPeriod();
					break;
				}
			}
			newUsp.setLockInPeriod(lockInPeriod);
			newUsp.insert();
		}
		//5、增加持仓日志
		UserPositionChangeRecord uspcr = new UserPositionChangeRecord();
		uspcr.setUserId(uns.getUserId());
		uspcr.setPositionId(newUsp.getId());
		uspcr.setStockCode(newUsp.getStockCode());
		uspcr.setStockName(newUsp.getStockName());
		uspcr.setDataChangeTypeCode(UserStockPositionChangeTypeEnum.NEW_SHARE_TRANSFER_POSITION.getCode());
		uspcr.setDataChangeTypeName(UserStockPositionChangeTypeEnum.NEW_SHARE_TRANSFER_POSITION.getName());
		uspcr.setNewContent(newUsp.toString());
		uspcr.setOperator(operator);
		uspcr.setIp(ip);
		uspcr.setIpAddress(ipAddress);
		uspcr.setCreateTime(now);
		uspcr.insert();
		//6、通过持仓id，处理融资信息
		BigDecimal actualSubscriptionAmount = uns.getBuyingPrice().multiply(new BigDecimal(uns.getAwardQuantity()));//实际认购金额 = 申购价格*中签数
		userFinancingInterestInfoService.doUserFinancingInterestInfoByPositionId(newUsp.getId(),actualSubscriptionAmount);
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股已转入持仓",operator,"恭喜您，您申购的新股【" + uns.getStockName() + "（" + uns.getStockCode() + "）】已申购成功转入持仓，请及时关注持仓信息。");						
		return Response.success();
	}
	
	/**
	 * 0元申购
	 */
	@Override
	public Response<Void> subscripNewShareWithZero(Integer userId, NewShareSubscripWithZeroParamVO param) {
		LambdaQueryWrapper<NewShare> lqw = new LambdaQueryWrapper<>();
		lqw.eq(NewShare::getStockCode, param.getStockCode());
		lqw.eq(NewShare::getStockType, param.getStockType());
		NewShare newShare = newShareMapper.selectOne(lqw);
		StockParamConfig spc =  this.sysParamConfigService.getSysParamConfig();
		if(newShare == null) {
			return Response.fail("申购失败，新股信息不存在");
		}
		if (!newShare.getIsShow()) {
			return Response.fail("申购失败，新股信息已隐藏");
		}
		if (newShare.getIsLock()) {
			return Response.fail("申购失败，新股信息已锁定");
		}
		if(!newShare.getEnableZeroSubscription()) {
			return Response.fail("申购失败，此产品未开放"+spc.getZeroSubscriptionName());
		}
		Date now = new Date();
		if(now.compareTo(newShare.getSubscriptionDeadline()) == 1) {
			return Response.fail("申购失败，此产品的申购截止日期为 " + new SimpleDateFormat("yyyy/MM/dd").format(newShare.getSubscriptionDeadline()));
		}
		if (spc.getZeroSubscriptionCount() > 0) {
			int count = this.lambdaQuery()
					.eq(UserNewShareSubscription::getStockCode, param.getStockCode())
					.eq(UserNewShareSubscription::getStockType, param.getStockType())
					.eq(UserNewShareSubscription::getUserId, userId)
					.eq(UserNewShareSubscription::getSubscriptionType, 0)
					.count();
			if(count >= spc.getZeroSubscriptionCount()) {
				return Response.fail("申购失败，此产品'"+spc.getZeroSubscriptionName()+"'次数不能超过" + spc.getZeroSubscriptionCount() + "次");
			}
		}
		
		UserNewShareSubscription unss = new UserNewShareSubscription();
		unss.setOrderSn(OrderNumberGenerator.create(4));
		unss.setUserId(userId);
		unss.setNewShareId(newShare.getId());
		unss.setStockCode(newShare.getStockCode());
		unss.setStockType(newShare.getStockType());
		unss.setStockName(newShare.getStockName());
		unss.setStockPlate(newShare.getStockPlate());
		unss.setBuyingPrice(newShare.getDiscountedPrice() == null || newShare.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1 ? newShare.getPrice() : newShare.getDiscountedPrice());
		unss.setSubscriptionType(0);
		unss.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(userId,"新股申购成功",userInfoService.getById(userId).getOperator(),"恭喜您，您已成功申购新股【"+newShare.getStockName()+"（"+newShare.getStockCode()+"）】，请及时关注中签结果");
		return Response.success();
	}

	/**
	 * 现金申购
	 */
	@Override
	public Response<Void> subscripNewShareWithCash(Integer userId, String ip, NewShareSubscripWithCashParamVO param) {
		if(param.getPurchaseQuantity() <= 0) {
			return Response.fail("请选择申购数量");
		}
		LambdaQueryWrapper<NewShare> lqw = new LambdaQueryWrapper<>();
		lqw.eq(NewShare::getStockCode, param.getStockCode());
		lqw.eq(NewShare::getStockType, param.getStockType());
		NewShare newShare = newShareMapper.selectOne(lqw);
		StockParamConfig spc =  this.sysParamConfigService.getSysParamConfig();
		if(newShare == null) {
			return Response.fail("申购失败，新股信息不存在");
		}
		if (!newShare.getIsShow()) {
			return Response.fail("申购失败，新股信息已隐藏");
		}
		if (newShare.getIsLock()) {
			return Response.fail("申购失败，新股信息已锁定");
		}
		if(!newShare.getEnableCashSubscription()) {
			return Response.fail("申购失败，此产品未开放"+spc.getCashSubscriptionName());
		}
		Date now = new Date();
		if(now.compareTo(newShare.getSubscriptionDeadline()) == 1) {
			return Response.fail("申购失败，此产品的申购截止日期为 " + new SimpleDateFormat("yyyy/MM/dd").format(newShare.getSubscriptionDeadline()));
		}
		if(param.getPurchaseQuantity() > newShare.getMaxBuyingShares()) {
			return Response.fail("申购失败，申购数量不能大于最大申购股数");
		}
		if(param.getPurchaseQuantity() > newShare.getIssueShares()) {
			return Response.fail("申购失败，申购数量不能大于发行总数");
		}
		if (spc.getCashSubscriptionCount() > 0) {
			int count = this.lambdaQuery()
					.eq(UserNewShareSubscription::getStockCode, param.getStockCode())
					.eq(UserNewShareSubscription::getStockType, param.getStockType())
					.eq(UserNewShareSubscription::getUserId, userId)
					.eq(UserNewShareSubscription::getSubscriptionType, 1)
					.count();
			
			if(count >= spc.getCashSubscriptionCount()) {
				return Response.fail("申购失败，此产品'"+spc.getCashSubscriptionName()+"'次数不能超过" + spc.getCashSubscriptionCount() + "次");
			}
		}
		BigDecimal buyingPrice = newShare.getDiscountedPrice() == null || newShare.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1 ? newShare.getPrice() : newShare.getDiscountedPrice();
		BigDecimal orderAmount = buyingPrice.multiply(new BigDecimal(param.getPurchaseQuantity())).setScale(2, RoundingMode.HALF_UP);
		CurrencyEnum currency = CurrencyEnum.getByStockType(param.getStockType());
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currency);
		UserInfo ui = userInfoService.getById(userId);
		BigDecimal availableAmt = ui.getAvailableAmt().multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
		if(availableAmt.compareTo(orderAmount) == -1) {
			return Response.fail("申购失败，您的现金余额不足，当前现金余额：" + availableAmt + currency.getCode());
		}
		UserNewShareSubscription unss = new UserNewShareSubscription();
		unss.setOrderSn(OrderNumberGenerator.create(5));
		unss.setUserId(userId);
		unss.setNewShareId(newShare.getId());
		unss.setStockCode(newShare.getStockCode());
		unss.setStockType(newShare.getStockType());
		unss.setStockName(newShare.getStockName());
		unss.setStockPlate(newShare.getStockPlate());
		unss.setBuyingPrice(buyingPrice);
		unss.setPurchaseQuantity(param.getPurchaseQuantity());
		unss.setSubscriptionType(1);
		unss.setSubscriptionAmount(orderAmount);
		unss.setExchangeRate(exchangeRate);
		unss.insert();
		StringBuilder deSummary = new StringBuilder();
		deSummary.append("现金申购新股：").append(newShare.getStockName()).append("(").append(newShare.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(newShare.getStockType())).append(")，申购价格：");
		deSummary.append(buyingPrice).append("，申购数量：").append(param.getPurchaseQuantity()).append("，申购记录id：").append(unss.getId());
		this.userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.SubscripNewShare, orderAmount, deSummary.toString(), currency, exchangeRate, ip, this.ipAddressService.getIpAddress(ip).getAddress2(), ui.getOperator());
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(userId,"新股申购成功",userInfoService.getById(userId).getOperator(),"恭喜您，您已成功申购新股【"+newShare.getStockName()+"（"+newShare.getStockCode()+"）】，请及时关注中签结果");
		return Response.success();
	}

	/**
	 * 融资申购
	 */
	@Override
	@Transactional
	public Response<Void> subscripNewShareWithFinancing(Integer userId, String ip,  NewShareSubscripWithFinancingParamVO param) {
		if(param.getSubscriptionAmount().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入申购金额");
		}
		Integer lever = param.getLever() == null || param.getLever() == 0 ? 1 : param.getLever();
		StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
		if(lever > 1) {
			String[] leverArr = stockParamConfig.getLevers().split("/");
			boolean isExists = false;
			for(String l : leverArr) {
				if(l.equals(String.valueOf(lever))) {
					isExists = true;
					break;
				}
			}
			if(!isExists) {
				return Response.fail("申购失败，您选择的杆杠位数错误");
			}
		}
		LambdaQueryWrapper<NewShare> lqw = new LambdaQueryWrapper<>();
		lqw.eq(NewShare::getStockCode, param.getStockCode());
		lqw.eq(NewShare::getStockType, param.getStockType());
		NewShare newShare = newShareMapper.selectOne(lqw);
		if(newShare == null) {
			return Response.fail("申购失败，新股信息不存在");
		}
		if (!newShare.getIsShow()) {
			return Response.fail("申购失败，新股信息已隐藏");
		}
		if (newShare.getIsLock()) {
			return Response.fail("申购失败，新股信息已锁定");
		}
		if(!newShare.getEnableFinancingSubscription()) {
			return Response.fail("申购失败，此产品未开放"+stockParamConfig.getFinancingSubscriptionName());
		}
		Date now = new Date();
		if(now.compareTo(newShare.getSubscriptionDeadline()) == 1) {
			return Response.fail("申购失败，此产品的申购截止日期为 " + new SimpleDateFormat("yyyy/MM/dd").format(newShare.getSubscriptionDeadline()));
		}
		if (stockParamConfig.getFinancingSubscriptionCount() > 0) {
			int count = this.lambdaQuery()
					.eq(UserNewShareSubscription::getStockCode, param.getStockCode())
					.eq(UserNewShareSubscription::getStockType, param.getStockType())
					.eq(UserNewShareSubscription::getUserId, userId)
					.eq(UserNewShareSubscription::getSubscriptionType, 2)
					.count();
			
			if(count >= stockParamConfig.getFinancingSubscriptionCount()) {
				return Response.fail("申购失败，此产品'"+stockParamConfig.getFinancingSubscriptionName()+"'次数不能超过" + stockParamConfig.getFinancingSubscriptionCount() + "次");
			}
		}
		CurrencyEnum currency = CurrencyEnum.getByStockType(param.getStockType());
		BigDecimal exchangeRate = this.sysParamConfigService.getExchangeRate(currency);
		UserInfo ui = this.userInfoService.getById(userId);
		BigDecimal availableAmt = ui.getAvailableAmt().multiply(exchangeRate);
		if(availableAmt.compareTo(param.getSubscriptionAmount()) == -1) {
			return Response.fail("申购失败，您的现金余额不足：" + param.getSubscriptionAmount() + currency.getCode());
		}
		BigDecimal buyingPrice = newShare.getDiscountedPrice() == null || newShare.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1 ? newShare.getPrice() : newShare.getDiscountedPrice();
		UserNewShareSubscription unss = new UserNewShareSubscription();
		unss.setOrderSn(OrderNumberGenerator.create(6));
		unss.setUserId(userId);
		unss.setNewShareId(newShare.getId());
		unss.setStockCode(newShare.getStockCode());
		unss.setStockType(newShare.getStockType());
		unss.setStockName(newShare.getStockName());
		unss.setStockPlate(newShare.getStockPlate());
		unss.setBuyingPrice(buyingPrice);
		unss.setSubscriptionAmount(param.getSubscriptionAmount());
		unss.setLever(param.getLever());
		unss.setSubscriptionType(2);
		unss.setExchangeRate(exchangeRate);
		unss.insert();
		StringBuilder deSummary = new StringBuilder();
		deSummary.append("申购新股(融资)：").append(newShare.getStockName()).append("(").append(newShare.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(newShare.getStockType())).append(")，申购价格：");
		deSummary.append(buyingPrice).append("，申购金额：").append(param.getSubscriptionAmount()).append("，杠杆：").append(lever).append("，申购记录id:").append(unss.getId());;
		this.userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.SubscripNewShare, param.getSubscriptionAmount(), deSummary.toString(), currency, exchangeRate, ip, this.ipAddressService.getIpAddress(ip).getAddress2(), ui.getOperator());
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(userId,"新股申购成功",userInfoService.getById(userId).getOperator(),"恭喜您，您已成功申购新股【"+newShare.getStockName()+"（"+newShare.getStockCode()+"）】，请及时关注中签结果");
		return Response.success();
	}

	@Override
	public void userNewShareSubscriptionList(Page<UserNewShareSubscriptionListVO> page, Integer userId, Integer subscriptionType) {
		this.userNewShareSubscriptionMapper.userNewShareSubscriptionList(page, userId, subscriptionType);
	}

	@Override
	public Response<UserNewShareSubscriptionDetailVO> userNewShareSubscriptionDetail(Integer id, Integer userId) {
		UserNewShareSubscription uss = this.lambdaQuery().eq(UserNewShareSubscription::getId, id).eq(UserNewShareSubscription::getUserId, userId).one();
		if(uss == null) {
			return Response.fail("申购记录不存在");
		}
		UserNewShareSubscriptionDetailVO vo = new UserNewShareSubscriptionDetailVO();
		vo.setId(uss.getId());
		vo.setOrderSn(uss.getOrderSn());
		vo.setStockCode(uss.getStockCode());
		vo.setStockName(uss.getStockName());
		vo.setStockType(uss.getStockType());
		vo.setStockPlate(uss.getStockPlate());
		vo.setSubscriptionAmount(uss.getSubscriptionAmount());
		vo.setBond(uss.getBond());
		vo.setBuyingPrice(uss.getBuyingPrice());
		vo.setPurchaseQuantity(uss.getPurchaseQuantity());
		vo.setAwardQuantity(uss.getAwardQuantity());
		vo.setSubscriptionStatus(uss.getSubscriptionStatus());
		vo.setSubscriptionTime(uss.getSubscriptionTime());
		vo.setSubscriptionTime(uss.getSubscriptionTime());
		vo.setSubscriptionType(uss.getSubscriptionType());
		vo.setLever(uss.getLever());
		vo.setAwardTime(uss.getAwardTime());
		vo.setPaymentTime(uss.getPaymentTime());
		vo.setTransferTime(uss.getTransferTime());
		if(uss.getSubscriptionStatus() != 2 && uss.getSubscriptionStatus() != 3) {
			vo.setPaymentDeadline(newShareMapper.selectById(uss.getNewShareId()).getPaymentDeadline());
		} else {
			vo.setPayAmount(vo.getBuyingPrice().multiply(new BigDecimal(vo.getAwardQuantity())));
		}
		return Response.successData(vo);
	}
	
	@Override
	@Transactional
	public Response<Void> userPay(Integer id, Integer userId, String ip, String operator) {
		UserNewShareSubscription uns = this.lambdaQuery().eq(UserNewShareSubscription::getId, id).eq(UserNewShareSubscription::getUserId, userId).one();
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 1) {
			return Response.fail("认缴失败，记录状态不是已中签");
		}
		Date now = new Date();
		NewShare newShare = this.newShareMapper.selectById(uns.getNewShareId());
		if(newShare == null) {
			return Response.fail("认缴失败，新股数据不存在");
		}
		if(newShare.getPaymentDeadline() == null) {
			return Response.fail("缴纳失败，此产品还未发布认缴时间");
		}
		if(now.compareTo(newShare.getPaymentDeadline()) == 1) {
			return Response.fail("缴纳失败，此产品的认缴截止日期为 " + new SimpleDateFormat("yyyy/MM/dd").format(newShare.getSubscriptionDeadline()));
		}
		CurrencyEnum currency = CurrencyEnum.getByStockType(uns.getStockType());
		BigDecimal exchangeRate = uns.getExchangeRate();
		String address = this.ipAddressService.getIpAddress(ip).getAddress2();
		BigDecimal orderAmount = uns.getBuyingPrice().multiply(new BigDecimal(uns.getAwardQuantity())); 
		UserInfo ui = this.userInfoService.getById(uns.getUserId());
		BigDecimal availableAmt = ui.getAvailableAmt().multiply(exchangeRate).setScale(2, RoundingMode.DOWN);
		if(availableAmt.compareTo(orderAmount) == -1) {
			return Response.fail("认缴失败，用户现金余额不足，当前现金余额：" + availableAmt + currency.getCode());
		}
		StringBuilder deSummary = new StringBuilder();
		deSummary.append("认缴新股：").append(newShare.getStockName()).append("(").append(newShare.getStockCode()).append(")(").append(StockTypeEnum.getNameByCode(uns.getStockType())).append(")，申购价格：");
		deSummary.append(uns.getBuyingPrice()).append("，中签数量：").append(uns.getAwardQuantity()).append("，申购记录id：" + id);
		this.userInfoService.updateUserAvailableAmt(uns.getUserId(), AmtDeTypeEnum.PayNewShare, orderAmount, deSummary.toString(), currency, exchangeRate, ip, address, operator);
		this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
			.set(UserNewShareSubscription::getSubscriptionStatus, 2)
			.set(UserNewShareSubscription::getPaymentTime, now).update();
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(2);
		unsol.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		unsol.setIp(ip);
		unsol.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股已认缴",operator,"恭喜您，您申购的新股【" + uns.getStockName() + "（" + uns.getStockCode() + "）】已确认缴纳，等待工作人员审核，请及时关注持仓信息。");				
		return Response.success();
	}

	/**
	 * 获取-用户新股认缴过期
	 */
	@Override
	public List<UserNewShareSubscription> getUserNewShareSubscriptionExpired() {
		return userNewShareSubscriptionMapper.getUserNewShareSubscriptionExpired();
	}

	/**
	 * 新股-用户取消
	 */
	@Override
	public Response<Void> userCancel(Integer id, Integer userId, String ip, String operator) {
		UserNewShareSubscription uns = this.lambdaQuery().eq(UserNewShareSubscription::getId, id).eq(UserNewShareSubscription::getUserId, userId).one();
		if(uns == null) {
			return Response.fail("申购记录不存在");
		}
		if(uns.getSubscriptionStatus() != 0) {
			return Response.fail("记录状态不是申购中");
		}
		this.lambdaUpdate().eq(UserNewShareSubscription::getId, id)
		.set(UserNewShareSubscription::getAwardQuantity, 0)
		.set(UserNewShareSubscription::getSubscriptionStatus, 6).update();
		String ipAddress = ipAddressService.getIpAddress(ip).getAddress2();
		if(uns.getSubscriptionType() != 0) {
			this.userInfoService.updateUserAvailableAmt(uns.getUserId(), 
					AmtDeTypeEnum.SubscripNewShareFail, 
					uns.getSubscriptionAmount(), 
					"新股申购已取消，申购记录id：" + id, 
					CurrencyEnum.getByStockType(uns.getStockType()), 
					uns.getExchangeRate(), ip, ipAddress, operator);
		}
		UserNewShareSubscriptionOperateLog unsol = new UserNewShareSubscriptionOperateLog();
		unsol.setSubscriptionId(uns.getId());
		unsol.setOperator(operator);
		unsol.setOperateType(4);
		unsol.setIpAddress(ipAddress);
		unsol.setIp(ip);
		unsol.insert();
		//发送站内信
		siteInternalMessageService.sendSiteInternalMessage(uns.getUserId(),"新股申购已取消",operator,"您申购【" + uns.getStockName() + "（" + uns.getStockCode() + "）】已取消。");				
		return Response.success();
	}

}
