package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFinancingCertification;
import entity.common.Response;
import vo.manager.UserFinancingCertificationListSearchVO;

/**
 * <p>
 * 用户融资认证信息 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-15
 */
public interface UserFinancingCertificationService extends IService<UserFinancingCertification> {
	
	void managerUserFinancingCertificationList(Page<UserFinancingCertification> page, UserFinancingCertificationListSearchVO param);

	Response<Void> managerUpdateFundingStatus(Integer id, Integer fundingStatus,  String ip, String operator);

	Response<Void> submitFinancingCertification(Integer userId, String ip);
}
