package service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.NewShare;
import entity.NewShareDataChangeRecord;
import entity.StockInfo;
import entity.common.Response;
import enums.NewShareDataChangeTypeEnum;
import enums.StockTypeEnum;
import mapper.NewShareMapper;
import service.IpAddressService;
import service.NewShareService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserInfoService;
import utils.StringUtil;
import vo.manager.NewShareVO;
import vo.manager.ServerNewShareSearchParamVO;
import vo.server.ServerNewShareVO;

/**
 * <p>
 * 新股表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-11-21
 */
@Service
public class NewShareServiceImpl extends ServiceImpl<NewShareMapper, NewShare> implements NewShareService {

	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private NewShareMapper newShareMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private StockInfoService stockInfoService;

	@Override
	@Transactional
	public Response<Void> add(NewShareVO param, String ip, String operator) {
		if (StringUtil.isEmpty(param.getStockName())) {
			return Response.fail("请输入新股名称");
		}
		if (StringUtil.isEmpty(param.getStockCode())) {
			return Response.fail("请输入新股代码");
		}
		if (StringUtil.isEmpty(param.getStockType())) {
			return Response.fail("请选择新股类型");
		}
		if (param.getPrice() == null || param.getPrice().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入新股价格");
		}
		if (param.getIssueShares() == null || param.getIssueShares() < 1) {
			return Response.fail("请输入发行总数");
		}
		if(param.getDiscountedPrice() != null && param.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("折扣价格错误，请重新输入");
		}
		if (param.getMaxBuyingShares() == null || param.getMaxBuyingShares() < 1) {
			return Response.fail("请输入新股最大申购数量");
		}
		if (param.getSubscriptionDeadline() == null) {
			return Response.fail("请输入申购截止时间");
		}
		if (param.getPaymentDeadline() == null) {
			return Response.fail("请输入认缴截止时间");
		}
		if(param.getEnableZeroSubscription() && (param.getStockType().equals(StockTypeEnum.HK.getCode()) || param.getStockType().equals(StockTypeEnum.US.getCode()))) {
			return Response.fail("美股和港股无法开通0元申购，请重新选择");
		}
		if(this.lambdaQuery().eq(NewShare::getStockCode, param.getStockCode()).eq(NewShare::getStockType, param.getStockType()).count() > 0) {
			return Response.fail("新股" + param.getStockType() + "." + param.getStockCode() + "已存在");
		}
		if(stockInfoService.lambdaQuery().eq(StockInfo::getStockCode, param.getStockCode()).eq(StockInfo::getStockType, param.getStockType()).count() > 0) {
			return Response.fail("新股" + param.getStockType() + "." + param.getStockCode() + "在上市股票数据中已存在");
		}
		NewShare ns = new NewShare();
		ns.setStockName(param.getStockName());
		ns.setStockCode(param.getStockCode());
		ns.setStockType(param.getStockType());
		ns.setStockPlate(param.getStockPlate());
		ns.setPrice(param.getPrice());
		ns.setIssueShares(param.getIssueShares());
		ns.setDiscountedPrice(param.getDiscountedPrice());
		ns.setMaxBuyingShares(param.getMaxBuyingShares());
		ns.setSubscriptionDeadline(param.getSubscriptionDeadline());
		ns.setPaymentDeadline(param.getPaymentDeadline());
		ns.setListingDate(param.getListingDate());
		ns.setIsLock(param.getIsLock());
		ns.setIsShow(param.getIsShow());
		ns.setEnableZeroSubscription(param.getEnableZeroSubscription());
		ns.setEnableCashSubscription(param.getEnableCashSubscription());
		ns.setEnableFinancingSubscription(param.getEnableFinancingSubscription());
		ns.setCreator(operator);
		ns.insert();
		String ipAddress = this.ipAddressService.getIpAddress(ip).getAddress2();
		NewShareDataChangeRecord nsdr = new NewShareDataChangeRecord();
		nsdr.setNewShareId(ns.getId());
		nsdr.setStockCode(ns.getStockCode());
		nsdr.setStockName(ns.getStockName());
		nsdr.setDataChangeTypeCode(NewShareDataChangeTypeEnum.ADD_NEW_SHARE.getCode());
		nsdr.setDataChangeTypeName(NewShareDataChangeTypeEnum.ADD_NEW_SHARE.getName());
		nsdr.setNewContent(ns.toString());
		nsdr.setOperator(operator);
		nsdr.setIp(ip);
		nsdr.setIpAddress(ipAddress);
		nsdr.insert();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> edit(NewShareVO param, String ip, String operator) {
		if (StringUtil.isEmpty(param.getStockName())) {
			return Response.fail("请输入新股名称");
		}
		if (param.getPrice() == null || param.getPrice().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入新股价格");
		}
		if (param.getIssueShares() == null || param.getIssueShares() < 1) {
			return Response.fail("请输入发行总数");
		}
		if(param.getDiscountedPrice() != null && param.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("折扣价格错误，请重新输入");
		}
		if (param.getMaxBuyingShares() == null || param.getMaxBuyingShares() < 1) {
			return Response.fail("请输入新股最大申购数量");
		}
		if (param.getSubscriptionDeadline() == null) {
			return Response.fail("请输入申购截止时间");
		}
		if (param.getPaymentDeadline() == null) {
			return Response.fail("请输入认缴截止时间");
		}
		if(param.getEnableZeroSubscription() == null) {
			return Response.fail("请选择是否开启0元申购");
		}
		if(param.getEnableCashSubscription() == null) {
			return Response.fail("请选择是否开启现金申购");
		}
		if(param.getEnableFinancingSubscription() == null) {
			return Response.fail("请选择是否开启融资申购");
		}
		if(param.getEnableZeroSubscription() && (param.getStockType().equals(StockTypeEnum.HK.getCode()) || param.getStockType().equals(StockTypeEnum.US.getCode()))) {
			return Response.fail("美股和港股无法开通0元申购，请重新选择");
		}
		NewShare old = this.getById(param.getId());
		if(old == null) {
			return Response.fail("保存失败，新股信息不存在");
		}
		LambdaUpdateWrapper<NewShare> lqw = new LambdaUpdateWrapper<>();
		StringBuilder oldContent = new StringBuilder();
		StringBuilder newContent = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(!old.getStockName().equals(param.getStockName())) {
			lqw.set(NewShare::getStockName, param.getStockName());
			oldContent.append("名称：").append(old.getStockName());
			newContent.append("名称：").append(param.getStockName());
		}
		if(StringUtil.isEmpty(old.getStockPlate())) {
			if(!StringUtil.isEmpty(param.getStockPlate())) {
				lqw.set(NewShare::getStockPlate, param.getStockPlate());
				oldContent.append("\n板块：");
				newContent.append("\n板块：").append(param.getStockName());
			}
		} else {
			if(StringUtil.isEmpty(param.getStockPlate())) {
				lqw.set(NewShare::getStockPlate, param.getStockPlate());
				oldContent.append("\n板块：").append(old.getStockPlate());
				newContent.append("\n板块：");
			} else if(!old.getStockPlate().equals(param.getStockPlate())) {
				lqw.set(NewShare::getStockPlate, param.getStockPlate());
				oldContent.append("\n板块：").append(old.getStockPlate());
				newContent.append("\n板块：").append(param.getStockName());
			}
		}
		if(old.getPrice().compareTo(param.getPrice()) != 0) {
			lqw.set(NewShare::getPrice, param.getPrice());
			oldContent.append("\n定价：").append(old.getPrice());
			newContent.append("\n定价：").append(param.getPrice());
		}
		if(!old.getIssueShares().equals(param.getIssueShares())) {
			lqw.set(NewShare::getIssueShares, param.getIssueShares());
			oldContent.append("\n发行总数：").append(old.getIssueShares());
			newContent.append("\n发行总数：").append(param.getIssueShares());
		}
		if(old.getDiscountedPrice() == null) {
			if(param.getDiscountedPrice() != null) {
				lqw.set(NewShare::getDiscountedPrice, param.getDiscountedPrice());
				oldContent.append("\n折扣价格：");
				newContent.append("\n折扣价格：").append(param.getDiscountedPrice());
			}
		} else {
			if(param.getDiscountedPrice() == null) {
				lqw.set(NewShare::getDiscountedPrice, param.getDiscountedPrice());
				oldContent.append("\n折扣价格：").append(old.getDiscountedPrice());
				newContent.append("\n折扣价格：");
			} else if(old.getDiscountedPrice().compareTo(param.getDiscountedPrice()) != 0) {
				lqw.set(NewShare::getDiscountedPrice, param.getDiscountedPrice());
				oldContent.append("\n折扣价格：").append(old.getDiscountedPrice());
				newContent.append("\n折扣价格：").append(param.getDiscountedPrice());
			}
		}
		if(!old.getMaxBuyingShares().equals(param.getMaxBuyingShares())) {
			lqw.set(NewShare::getMaxBuyingShares, param.getMaxBuyingShares());
			oldContent.append("\n最大申购数量：").append(old.getMaxBuyingShares());
			newContent.append("\n最大申购数量：").append(param.getMaxBuyingShares());
		}
		if(old.getSubscriptionDeadline().compareTo(param.getSubscriptionDeadline()) != 0) {
			lqw.set(NewShare::getSubscriptionDeadline, param.getSubscriptionDeadline());
			oldContent.append("\n申购截止日期：").append(sdf.format(old.getSubscriptionDeadline()));
			newContent.append("\n申购截止日期：").append(sdf.format(param.getSubscriptionDeadline()));
		}
		if(old.getPaymentDeadline() == null) {
			if(param.getPaymentDeadline() != null) {
				lqw.set(NewShare::getPaymentDeadline, param.getPaymentDeadline());
				oldContent.append("\n认缴截止日期：");
				newContent.append("\n认缴截止日期：").append(sdf.format(param.getPaymentDeadline()));
			}
		} else {
			if(old.getPaymentDeadline().compareTo(param.getPaymentDeadline()) != 0) {
				lqw.set(NewShare::getPaymentDeadline, param.getPaymentDeadline());
				oldContent.append("\n认缴截止日期：").append(sdf.format(old.getPaymentDeadline()));
				newContent.append("\n认缴截止日期：").append(sdf.format(param.getPaymentDeadline()));
			}
		}
		if(old.getListingDate() == null) {
			if(param.getListingDate() != null) {
				lqw.set(NewShare::getListingDate, param.getListingDate());
				oldContent.append("\n上市时间：");
				newContent.append("\n上市时间：").append(sdf.format(param.getListingDate()));
			}
		} else {
			if(param.getListingDate() == null) {
				lqw.set(NewShare::getListingDate, param.getListingDate());
				oldContent.append("\n上市时间：").append(sdf.format(old.getListingDate()));
				newContent.append("\n上市时间：");
			} else if(old.getListingDate().compareTo(param.getListingDate()) != 0) {
				lqw.set(NewShare::getListingDate, param.getListingDate());
				oldContent.append("\n上市时间：").append(sdf.format(old.getListingDate()));
				newContent.append("\n上市时间：").append(sdf.format(param.getListingDate()));
			}
		}
		if(old.getIsLock() != param.getIsLock()) {
			lqw.set(NewShare::getIsLock, param.getIsLock());
			oldContent.append("\n是否锁定：").append(old.getIsLock() ? "是" : "否");
			newContent.append("\n是否锁定：").append(param.getIsLock() ? "是" : "否");
		}
		if(old.getIsShow() != param.getIsShow()) {
			lqw.set(NewShare::getIsShow, param.getIsShow());
			oldContent.append("\n是否显示：").append(old.getIsShow() ? "是" : "否");
			newContent.append("\n是否显示：").append(param.getIsShow() ? "是" : "否");
		}
		if(old.getEnableZeroSubscription() != param.getEnableZeroSubscription()) {
			lqw.set(NewShare::getEnableZeroSubscription, param.getEnableZeroSubscription());
			oldContent.append("\n是否开启0元申购：").append(old.getEnableZeroSubscription() ? "是" : "否");
			newContent.append("\n是否开启0元申购：").append(param.getEnableZeroSubscription() ? "是" : "否");
		}
		if(old.getEnableCashSubscription() != param.getEnableCashSubscription()) {
			lqw.set(NewShare::getEnableCashSubscription, param.getEnableCashSubscription());
			oldContent.append("\n是否开启现金申购：").append(old.getEnableCashSubscription() ? "是" : "否");
			newContent.append("\n是否开启现金申购：").append(param.getEnableCashSubscription() ? "是" : "否");
		}
		if(old.getEnableFinancingSubscription() != param.getEnableFinancingSubscription()) {
			lqw.set(NewShare::getEnableFinancingSubscription, param.getEnableFinancingSubscription());
			oldContent.append("\n是否开启融资申购：").append(old.getEnableFinancingSubscription() ? "是" : "否");
			newContent.append("\n是否开启融资申购：").append(param.getEnableFinancingSubscription() ? "是" : "否");
		}
		if(StringUtil.isEmpty(lqw.getSqlSet())) {
			return Response.success();
		}
		lqw.eq(NewShare::getId, param.getId());
		this.update(lqw);
		String ipAddress = this.ipAddressService.getIpAddress(ip).getAddress2();
		NewShareDataChangeRecord nsdr = new NewShareDataChangeRecord();
		nsdr.setNewShareId(old.getId());
		nsdr.setStockCode(old.getStockCode());
		nsdr.setStockName(old.getStockName());
		nsdr.setDataChangeTypeCode(NewShareDataChangeTypeEnum.EDIT_NEW_SHARE.getCode());
		nsdr.setDataChangeTypeName(NewShareDataChangeTypeEnum.EDIT_NEW_SHARE.getName());
		nsdr.setOldContent(oldContent.toString());
		nsdr.setNewContent(newContent.toString());
		nsdr.setOperator(operator);
		nsdr.setIp(ip);
		nsdr.setIpAddress(ipAddress);
		nsdr.insert();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> delete(Integer id, String ip, String operator) {
		NewShare old = this.getById(id);
		if(old == null) {
			return Response.fail("删除失败，新股信息不存在");
		}
		old.deleteById();
		String ipAddress = this.ipAddressService.getIpAddress(ip).getAddress2();
		NewShareDataChangeRecord nsdr = new NewShareDataChangeRecord();
		nsdr.setNewShareId(old.getId());
		nsdr.setStockCode(old.getStockCode());
		nsdr.setStockName(old.getStockName());
		nsdr.setDataChangeTypeCode(NewShareDataChangeTypeEnum.DEL_NEW_SHARE.getCode());
		nsdr.setDataChangeTypeName(NewShareDataChangeTypeEnum.DEL_NEW_SHARE.getName());
		nsdr.setOldContent(old.toString());
		nsdr.setOperator(operator);
		nsdr.setIp(ip);
		nsdr.setIpAddress(ipAddress);
		nsdr.insert();
		return Response.success();
	}

	@Override
	public void getNewSharePageByStockType(Page<ServerNewShareVO> page, ServerNewShareSearchParamVO param) {
		this.newShareMapper.getNewShareServerNewShareVO(page, param);
	}
}
