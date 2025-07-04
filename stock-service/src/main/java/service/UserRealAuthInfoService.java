package service;

import entity.UserRealAuthInfo;
import entity.common.Response;
import vo.server.AddUserRealAuthInfoVO;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户认证信息表 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-13
 */
public interface UserRealAuthInfoService extends IService<UserRealAuthInfo> {
	/**
	 * 管理系统->用户管理->认证审核->修改认证状态
	 */
	Response<Void> managerUpdateRealAuthStatus(Integer id, Integer realAuthStatus,  String ip, String operator);
	
	Response<Void> submitRealAuthInfo(Integer userId, AddUserRealAuthInfoVO param, String ip);
}
