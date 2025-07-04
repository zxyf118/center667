package service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import config.RedisDbTypeEnum;
import constant.SysConstant;
import entity.SysUser;
import entity.SysUserLoginLog;
import entity.common.Response;
import mapper.SysUserMapper;
import redis.RedisKeyPrefix;
import service.IpAddressService;
import service.SysUserService;
import utils.GoogleAuthUtil;
import utils.NumberUtil;
import utils.PasswordGenerator;
import utils.RedisDao;
import vo.common.OnlineUserVO;
import vo.common.SysTokenVO;
import vo.manager.SysUserLoginResponseVO;

/**
 * <p>
 * 管理系统用户表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private RedisDao redisDao;
	
	@Override
	public Response<SysUserLoginResponseVO> login(String username, String loginPwd, String code, String ip) {
		SysUser user = this.lambdaQuery().eq(SysUser::getUsername, username).one();
		if(user == null) {
			 return Response.fail("账号不正确，请重新输入");
		}
		if(!PasswordGenerator.generate(SysConstant.PASSWORD_PREFIX, loginPwd).equals(user.getLoginPwd())){
            return Response.fail("登录密码不正确，请重新输入");
        }
//		if (!username.equals(SysConstant.MANAGER_ACCOUNT_NAME)) {
//			if (null == code) {
//                return Response.fail("请输入安全码");
//            }
//            if (!NumberUtil.isNumber(code)) {
//                return Response.fail("安全码格式错误");
//            }
//            if (StringUtils.isBlank(user.getGoogleKey())) {
//                return Response.fail("找管理员设置安全码");
//            }
//            if (!GoogleAuthUtil.check(user.getGoogleKey(), Integer.parseInt(code))) {
//                return Response.fail("安全码不正确");
//            }
//		}
		if(user.getUserStatus() == 0) {
			 return Response.fail("您的账号已停用，请联系系统管理员");
		}
		String sysToken = PasswordGenerator.createSysUserToken(user.getId());
		String tokenKey = RedisKeyPrefix.getSysTokenKey(sysToken);
		SysTokenVO st  = new SysTokenVO();
		st.setLastToken(sysToken);
		st.setSysUser(user);
		redisDao.setBean(RedisDbTypeEnum.DEFAULT, tokenKey, st, 1, TimeUnit.DAYS);
		
		String onlineSysUserKey = RedisKeyPrefix.getOnlineSysUserKey(user.getId());
		OnlineUserVO ou = redisDao.getBean(RedisDbTypeEnum.DEFAULT, onlineSysUserKey, OnlineUserVO.class);
		if(ou == null) {
			ou = new OnlineUserVO();
		} else {
            redisDao.del(RedisDbTypeEnum.DEFAULT, RedisKeyPrefix.getSysTokenKey(ou.getLatestToken()));
		}
		ou.setCurrentTimestamp(System.currentTimeMillis());
		ou.setLatestToken(sysToken);
		redisDao.setBean(RedisDbTypeEnum.DEFAULT, onlineSysUserKey, ou, 1, TimeUnit.DAYS);
		SysUserLoginLog sysUserLoginLog = new SysUserLoginLog();
        sysUserLoginLog.setLoginIp(ip);
        sysUserLoginLog.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
        sysUserLoginLog.setSysUserId(user.getId());
        sysUserLoginLog.setSysRealName(user.getRealName());
        sysUserLoginLog.setSysUserName(user.getUsername());
        sysUserLoginLog.insert();
        SysUserLoginResponseVO vo = new SysUserLoginResponseVO();
        vo.setUsername(user.getUsername());
        vo.setToken(sysToken);
        vo.setRealName(user.getRealName());
		return Response.successData(vo);
	}

	@Override
	public Response<Void> logout(String token, Integer sysUserId) {
		String onlineSysUserKey = RedisKeyPrefix.getOnlineSysUserKey(sysUserId);
		redisDao.del(RedisDbTypeEnum.DEFAULT, onlineSysUserKey);
		String tokenKey = RedisKeyPrefix.getSysTokenKey(token);
		redisDao.del(RedisDbTypeEnum.DEFAULT, tokenKey);
		return Response.success();
	}

}
