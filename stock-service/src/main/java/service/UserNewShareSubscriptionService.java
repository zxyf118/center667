package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserNewShareSubscription;
import entity.common.Response;
import vo.manager.NewShareSubscriptionListVO;
import vo.manager.NewShareSubscriptionSearchParamVO;
import vo.server.NewShareSubscripWithCashParamVO;
import vo.server.NewShareSubscripWithFinancingParamVO;
import vo.server.NewShareSubscripWithZeroParamVO;
import vo.server.UserNewShareSubscriptionDetailVO;
import vo.server.UserNewShareSubscriptionListVO;

/**
 * <p>
 * 新股申购记录表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface UserNewShareSubscriptionService extends IService<UserNewShareSubscription> {
	
	void managerList(Page<NewShareSubscriptionListVO> page, NewShareSubscriptionSearchParamVO param);
	
	Response<Void> managerWin(Integer id, Integer awardQuantity, String ip, String operator);
	
	Response<Void> managerFail(Integer id, String ip, String operator);
	
	Response<Void> managerPay(Integer id, String ip, String operator);
	
	Response<Void> managerTransfer(Integer id, String ip, String operator);
	
	Response<Void> subscripNewShareWithZero(Integer userId, NewShareSubscripWithZeroParamVO param);
	
	Response<Void> subscripNewShareWithCash(Integer userId, String ip, NewShareSubscripWithCashParamVO param);
	
	Response<Void> subscripNewShareWithFinancing(Integer userId, String ip, NewShareSubscripWithFinancingParamVO param);
	
	void userNewShareSubscriptionList(Page<UserNewShareSubscriptionListVO> page, Integer userId, Integer subscriptionType);
	
	Response<UserNewShareSubscriptionDetailVO> userNewShareSubscriptionDetail(Integer id, Integer userId);
	
	Response<Void> userPay(Integer id, Integer userId, String ip, String operator);

	/**
	 * 获取-用户新股认缴过期
	 * @return
	 */
	List<UserNewShareSubscription> getUserNewShareSubscriptionExpired();

	Response<Void> userCancel(Integer id, Integer loginId, String ip, String operator);
}
