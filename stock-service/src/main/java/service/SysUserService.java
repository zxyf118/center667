package service;

import com.baomidou.mybatisplus.extension.service.IService;

import entity.SysUser;
import entity.common.Response;
import vo.manager.SysUserLoginResponseVO;

/**
 * <p>
 * 管理系统用户表 服务类
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
public interface SysUserService extends IService<SysUser> {
	Response<SysUserLoginResponseVO> login( String username, String loginPwd, String code, String ip);
	Response<Void> logout(String token, Integer sysUserId);
}
