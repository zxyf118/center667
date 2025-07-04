package service;

import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserBankInfo;
import entity.common.Response;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-11-05
 */
public interface UserBankInfoService extends IService<UserBankInfo> {
	
	Response<Void> add(Integer userId, UserBankInfo userBankInfo, String ip, String operator);
	
	Response<Void> edit(UserBankInfo userBankInfo, String ip, String operator);
}
