package service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.IpAddress;
import entity.UserDataChangeRecord;
import entity.UserFinancingCertification;
import entity.UserInfo;
import entity.common.Response;
import enums.UserDataChangeTypeEnum;
import enums.UserFundingStatusEnum;
import enums.UserRealAuthStatusEnum;
import mapper.UserFinancingCertificationMapper;
import service.IpAddressService;
import service.UserFinancingCertificationService;
import service.UserInfoService;
import vo.manager.UserFinancingCertificationListSearchVO;

/**
 * <p>
 * 用户融资认证信息 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-12-15
 */
@Service
public class UserFinancingCertificationServiceImpl extends ServiceImpl<UserFinancingCertificationMapper, UserFinancingCertification> implements UserFinancingCertificationService {

	@Resource
	private UserFinancingCertificationMapper userFinancingCertificationMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Override
	public void managerUserFinancingCertificationList(Page<UserFinancingCertification> page,
			UserFinancingCertificationListSearchVO param) {
		userFinancingCertificationMapper.managerUserFinancingCertificationList(page, param);
	}

	@Override
	@Transactional
	public Response<Void> managerUpdateFundingStatus(Integer id, Integer fundingStatus, String ip, String operator) {
		if(fundingStatus != UserFundingStatusEnum.REVIEWED.getCode() && fundingStatus != UserFundingStatusEnum.FAILED.getCode()) {
			return Response.fail("状态参数错误");
		}
		UserFinancingCertification ufc = this.getById(id);
		if(ufc == null) {
			return Response.fail("记录不存在");
		}
		UserInfo user = userInfoService.getById(ufc.getUserId());
		if(user == null) {
			return Response.fail("用户信息错误");
		}
		if(ufc.getFundingStatus() != UserFundingStatusEnum.APPLYING.getCode()) {
			return Response.fail("该记录状态不是审核中，修改失败");
		}
		userInfoService.lambdaUpdate().eq(UserInfo::getId, user.getId()).set(UserInfo::getFundingStatus, fundingStatus).update();
		Date now = new Date();
		this.lambdaUpdate()
			.set(UserFinancingCertification::getFundingStatus, fundingStatus)
			.set(UserFinancingCertification::getOperateTime, now)
			.set(UserFinancingCertification::getOperator, operator)
			.eq(UserFinancingCertification::getId, id).update();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(user.getId());
		udcr.setCreateTime(now);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.FUNDING_STATUS.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.FUNDING_STATUS.getName());
		udcr.setOldContent(UserFundingStatusEnum.getNameByCode(user.getFundingStatus()));
		udcr.setNewContent(UserFundingStatusEnum.getNameByCode(fundingStatus));
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}

	@Override
	public Response<Void> submitFinancingCertification(Integer userId, String ip) {
		UserInfo ui = this.userInfoService.getById(userId);
		if(ui.getRealAuthStatus() != UserRealAuthStatusEnum.REVIEWED.getCode()) {
			return Response.fail("融资开户需实名认证");
		}
		if (ui.getFundingStatus() == UserFundingStatusEnum.APPLYING.getCode()) {
			return Response.fail("融资开户审核中，请等待");
		}
		if (ui.getFundingStatus() == UserFundingStatusEnum.REVIEWED.getCode()) {
			return Response.fail("融资开户已审核，无需申请");
		}
		Date now = new Date();
		UserInfo updateFundingStatus = new UserInfo();
		updateFundingStatus.setId(userId);
		updateFundingStatus.setFundingStatus(UserFundingStatusEnum.APPLYING.getCode());
		updateFundingStatus.updateById();
		UserFinancingCertification ufc = new UserFinancingCertification();
		ufc.setUserId(userId);
		ufc.setRequestTime(now);
		ufc.insert();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(ui.getId());
		udcr.setCreateTime(now);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.FUNDING_STATUS.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.FUNDING_STATUS.getName());
		udcr.setOldContent(UserFundingStatusEnum.NOT.getName());
		udcr.setNewContent(UserFundingStatusEnum.APPLYING.getName());
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(ui.getOperator());
		udcr.insert();
		return Response.success();
	}

}
