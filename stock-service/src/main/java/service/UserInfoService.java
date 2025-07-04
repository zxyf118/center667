package service;

import java.math.BigDecimal;
import java.util.List;

import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponseBody.ResultObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserInfo;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import vo.common.ChildAndParentVO;
import vo.common.TokenUserVO;
import vo.manager.AddUserParamVO;
import vo.manager.EditUserParamVO;
import vo.manager.UserAmtDetailVO;
import vo.manager.UserListSearchParamVO;
import vo.server.AliyunInitFaceVerifyParamVO;
import vo.server.AssetsDetailVO;
import vo.server.RegisterParamVO;

/**
 * <p>
 * 会员信息服务类
 * </p>
 *
 * @author
 * @since 2024-10-11
 */
public interface UserInfoService extends IService<UserInfo> {

	/**
	 * 会员上级关系查询
	 * @param userId 本级会员ID
	 * @return
	 */
	List<ChildAndParentVO> pNextParentUsers(Integer userId);
	
	/**
	 * 代理上级关系查询
	 * @param agentId 本级代理ID
	 * @return
	 */
	List<ChildAndParentVO> pNextParentAgents(Integer agentId);
	
	/**
	 * 获取用户资金信息
	 * @param userId
	 * @return
	 */
	UserAmtDetailVO getUserAmtDetail(Integer userId);
	
	/**
	 * 用户资金流水变更通用方法
	 * @param userId
	 * @param deType
	 * @param amt
	 * @param deSummary
	 * @param currency
	 * @param ip
	 * @param ipAddress
	 * @param operator
	 */
	void updateUserAvailableAmt(Integer userId,  AmtDeTypeEnum deType, BigDecimal amt, String deSummary, CurrencyEnum currency, BigDecimal exchangeRate, String ip, String ipAddress, String operator);
	
	/**
	 * 用户注册验证
	 * @param param
	 * @return
	 */
	Response<Void> registerVerify(RegisterParamVO param);
	
	/**
	 * 用户注册
	 * @param param
	 * @param ip
	 * @return
	 */
	Response<Void> register(RegisterParamVO param, String ip);

	/**
	 * 会员登录
	 * @param areaCode
	 * @param loginAccount
	 * @param loginPwd
	 * @param loginType
	 * @param ip
	 * @param requestUrl
	 * @return
	 */
	Response<TokenUserVO> login(String areaCode, String loginAccount, String loginPwd, int loginType, String ip, String requestUrl);
	
	Response<Void> logout(Integer userId, String ip, String requestUrl);

	/**
	 * 管理系统->用户管理->用户列表->查询
	 * @param page
	 * @param vo
	 * @return
	 */
	void managerUserList(Page<UserInfo> page,  UserListSearchParamVO vo);
	
	/**
	 * 管理系统->用户管理->用户列表->添加用户
	 * @param vo
	 * @return
	 */
	Response<Void> managerUserAdd(AddUserParamVO vo, String ip, String operator);
	
	/**
	 * 管理系统->用户管理->用户列表->编辑用户
	 * @param vo
	 * @return
	 */
	Response<Void> managerUserEdit(EditUserParamVO vo, String ip, String operator);
	
	/**
	 * 强制下线
	 * @param userId
	 * @return
	 */
	Response<Void> forcedOffline(Integer userId);
	
	Response<AssetsDetailVO> assetsDetail(Integer userId);

	Response<Void> modifyPhone(Integer userId, String areaCode, String phone, String ip, String operator);
	
	Response<Void> modifyEmail(Integer userId, String emmail, String ip, String operator);
	
	Response<Void> modifyLoginPwd(Integer userId, String oldLoginPwd, String newLoginPwd, String ip, String operator);
	
	Response<Void> modifyFundPwd(Integer userId, String oldPwd, String newFundPwd, String ip, String operator);

	Response<Void> pwdVerify(Integer userId, String pwd, int pwdType);

	/**
	 * 阿里云-发起人脸认证
	 * @param param
	 * @return
	 */
	Response<ResultObject> doAliyunInitFaceVerify(AliyunInitFaceVerifyParamVO param);

	/**
	 * 阿里云-获取人脸认证结果
	 * @param certifyId
	 * @return
	 */
	Response<String> doAliyunDescribeFaceVerify(String certifyId);

	/**
	 * 执行-人脸认证业务
	 * @param certifyId
	 * @param passed
	 * @return
	 */
	Response<String> doFaceVerifyBusiness(String certifyId, String passed);

	/**
	 * 忘记密码(人脸认证-修改密码)
	 * @param certifyId
	 * @return
	 */
	Response<Void> doFaceVerifyModifyLoginPwd(String certifyId,String newLoginPwd,String ip);

}
