package service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.sdk.service.cloudauth20190307.models.DescribeFaceVerifyResponse;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponse;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponseBody.ResultObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.json.JSONUtil;
import config.RedisDbTypeEnum;
import constant.Constant;
import entity.AgentInfo;
import entity.AgentInvitationCode;
import entity.AgentMemberInfo;
import entity.Country;
import entity.IpAddress;
import entity.UserAmtChangeRecord;
import entity.UserDataChangeRecord;
import entity.UserInfo;
import entity.UserLoginLog;
import entity.UserNewShareSubscription;
import entity.UserStockClosingPosition;
import entity.UserStockPending;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import enums.UserDataChangeTypeEnum;
import enums.UserFundingStatusEnum;
import enums.UserRealAuthStatusEnum;
import mapper.AgentMemberInfoMapper;
import mapper.UserInfoMapper;
import redis.RedisKeyPrefix;
import service.AgentInfoService;
import service.AgentInvitationCodeService;
import service.CountryService;
import service.IpAddressService;
import service.SysParamConfigService;
import service.UserFinancingCertificationService;
import service.UserInfoService;
import service.UserNewShareSubscriptionService;
import service.UserStockClosingPositionService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.PasswordGenerator;
import utils.RedisDao;
import utils.SinaApi;
import utils.StringUtil;
import utils.aliyun.AliyunFaceUtil;
import utils.aliyun.DescribeFaceVerify;
import utils.aliyun.InitFaceVerify;
import vo.common.ChildAndParentVO;
import vo.common.OnlineUserVO;
import vo.common.StockQuotesVO;
import vo.common.TokenUserVO;
import vo.manager.AddUserParamVO;
import vo.manager.EditUserParamVO;
import vo.manager.UserAmtDetailVO;
import vo.manager.UserListSearchParamVO;
import vo.server.AliyunInitFaceVerifyParamVO;
import vo.server.AssetsDetailVO;
import vo.server.RegisterParamVO;
import vo.server.UserLoginFailedTimesVO;

/**
 * <p>
 *  会员信息服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-11
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private CountryService countryService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private AgentInvitationCodeService agentInvitationCodeService;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Resource
	private AgentMemberInfoMapper agentMemberInfoMapper;
	
	@Resource
	private AgentInfoService agentInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private UserFinancingCertificationService userFinancingCertificationService;
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@Resource
	private UserNewShareSubscriptionService userNewShareSubscriptionService;
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;

	@Override
	public List<ChildAndParentVO> pNextParentUsers(Integer userId) {
	//	String key = RedisKeyPrefix.getPNextSupperUsersKey(userId);
		//List<ChildAndParentVO> uaLst = redisDao.getBeanList(RedisDbTypeEnum.DEFAULT, key, ChildAndParentVO.class);
		//if(uaLst == null) {
		List<ChildAndParentVO> uaLst = userInfoMapper.pNextParentUsers(userId);
		//	redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, uaLst, 1, TimeUnit.DAYS);
		//}
		return uaLst;
	}
	
	@Override
	public List<ChildAndParentVO> pNextParentAgents(Integer agentId) {
		//String key = RedisKeyPrefix.getPNextSupperAgentsKey(agentId);
		//List<ChildAndParentVO> uaLst = redisDao.getBeanList(RedisDbTypeEnum.DEFAULT, key, ChildAndParentVO.class);
		//if(uaLst == null) {
			List<ChildAndParentVO> uaLst = userInfoMapper.pNextParentAgents(agentId);
			//redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, uaLst, 1, TimeUnit.DAYS);
		//}
		return uaLst;
	}
	@Override
	public UserAmtDetailVO getUserAmtDetail(Integer userId) {
		UserAmtDetailVO vo = new UserAmtDetailVO();
		UserInfo user = this.getById(userId);
		if(user == null) {
			return vo;
		}
		vo.setUserId(user.getId());
		vo.setAccountType(user.getAccountType());
		vo.setAvailableAmt(user.getAvailableAmt());
		BigDecimal unavailableWithdrawalAmt = this.userInfoMapper.getUserUnavailableWithdrawalAmt(userId);
		if(unavailableWithdrawalAmt.compareTo(user.getAvailableAmt()) == 1) {
			unavailableWithdrawalAmt = user.getAvailableAmt();
		}
		vo.setAvailableWithdrawalAmt(user.getAvailableAmt().subtract(unavailableWithdrawalAmt));
		vo.setDetentionAmt(user.getDetentionAmt());
		vo.setTradingFrozenAmt(user.getTradingFrozenAmt());
		vo.setIpoAmt(user.getIpoAmt());
		vo.setAgentId(user.getAgentId());
		if(user.getAgentId() > 0) {
			AgentInfo agentInfo = this.agentInfoService.getById(user.getAgentId());
			vo.setAgentName(agentInfo.getAgentName());
		}
		BigDecimal totalAmt = user.getAvailableAmt().add(user.getTradingFrozenAmt()).add(user.getIpoAmt()).subtract(user.getDetentionAmt());
		vo.setTotalAmt(totalAmt);
		vo.setUsdExchangeRate(this.sysParamConfigService.getExchangeRate(CurrencyEnum.USD));
		vo.setHkdExchangeRate(this.sysParamConfigService.getExchangeRate(CurrencyEnum.HKD));
		return vo;
	}
	@Override
	@Transactional
	public void updateUserAvailableAmt(Integer userId,  AmtDeTypeEnum deType, BigDecimal amt, String deSummary, CurrencyEnum currency, BigDecimal exchangeRate, String ip, String ipAddress, String operator) {
		if (amt.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
		BigDecimal deAmt, deForeignAmt;
		switch(currency) {
		case CNY:
		default:
			deAmt = amt;
			deForeignAmt = null;
			break;
		case HKD:
		case USD:
			deAmt = amt.divide(exchangeRate, 2, RoundingMode.HALF_UP);
			deForeignAmt = amt;
			break;
		}
		UserInfo currentUser = this.getById(userId);
		StringBuilder setSql = new StringBuilder();
		BigDecimal beAmt = currentUser.getAvailableAmt();
		BigDecimal afAmt = currentUser.getAvailableAmt();
		BigDecimal beTradingFrozenAmt = currentUser.getTradingFrozenAmt();
		BigDecimal afTradingFrozenAmt = currentUser.getTradingFrozenAmt();
		BigDecimal beIpoAmt = currentUser.getIpoAmt();
		BigDecimal afIpoAmt = currentUser.getIpoAmt();
		switch(deType) {
		case ManualRecharge:
		case ClosingPosition:
		case ReturnFromNewShare:
		case BankCardWithdrawalDeclined:
		case BankCardWithdrawalCnnel:
		default:
			setSql.append("available_amt=available_amt+").append(deAmt);
			afAmt = afAmt.add(deAmt);
			break;
		case BankCardWithdrawalApplication:
		case ManualDeduction:
		case PayNewShare:
		case UserInterest:
		case FinancingRepayment:
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			afAmt = afAmt.subtract(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case BuyStock:
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			setSql.append(",trading_frozen_amt=trading_frozen_amt+").append(deAmt);
			afAmt = afAmt.subtract(deAmt);
			afTradingFrozenAmt = afTradingFrozenAmt.add(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case TransferPosition:
			setSql.append("trading_frozen_amt=case when (trading_frozen_amt-").append(deAmt).append(")<0 then 0 else (trading_frozen_amt-").append(deAmt).append(") end");
			afTradingFrozenAmt = afTradingFrozenAmt.subtract(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case CanneledPending:
		case RejectedPending:
			setSql.append("available_amt=available_amt+").append(deAmt);
			setSql.append(",trading_frozen_amt=case when (trading_frozen_amt-").append(deAmt).append(")<0 then 0 else (trading_frozen_amt-").append(deAmt).append(") end");
			afAmt = afAmt.add(deAmt);
			afTradingFrozenAmt = afTradingFrozenAmt.subtract(deAmt);
			break;
		case SubscripNewShare:
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			setSql.append(",ipo_amt=ipo_amt+").append(deAmt);
			afAmt = afAmt.subtract(deAmt);
			afIpoAmt = afIpoAmt.add(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case SubscripNewShareFail:
			setSql.append("ipo_amt=case when (ipo_amt-").append(deAmt).append(")<0 then 0 else (ipo_amt-").append(deAmt).append(") end");
			setSql.append(",available_amt=available_amt+").append(deAmt);
			afAmt = afAmt.add(deAmt);
			afIpoAmt = afIpoAmt.subtract(deAmt);
			break;
		case WinNewShare:
			setSql.append("ipo_amt=case when (ipo_amt-").append(deAmt).append(")<0 then 0 else (ipo_amt-").append(deAmt).append(") end");
			afIpoAmt = afIpoAmt.subtract(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		}
		UpdateWrapper<UserInfo> uw = new UpdateWrapper<>();
		uw.setSql(setSql.toString());
		uw.eq("id", userId);
		this.update(uw);
		UserAmtChangeRecord ucr = new UserAmtChangeRecord();
		ucr.setUserId(userId);
		ucr.setDeType(deType.getCode());
		ucr.setDeTypeName(deType.getName());
		ucr.setBeAmt(beAmt);
		ucr.setAfAmt(afAmt);
		ucr.setDeCnyAmt(deAmt);
		ucr.setDeForeignAmt(deForeignAmt);
		ucr.setBeTradingFrozenAmt(beTradingFrozenAmt);
		ucr.setAfTradingFrozenAmt(afTradingFrozenAmt);
		ucr.setBeIpoAmt(beIpoAmt);
		ucr.setAfIpoAmt(afIpoAmt);
		ucr.setDeSummary(deSummary);
		ucr.setCurrency(currency.getCode());
		ucr.setAddIp(ip);
		ucr.setAddAddress(ipAddress);
		ucr.setOperator(operator);
		ucr.setExchangeRate(exchangeRate);
		ucr.insert();
	}
	
	@Override
	public Response<Void> registerVerify(RegisterParamVO param) {
		if(StringUtil.isEmpty(param.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(param.getPhone()) || !StringUtil.isNumber(param.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(param.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		Country country = countryService.getCountryByAreaCode(param.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		int count = this.lambdaQuery().eq(UserInfo::getAreaCode, param.getAreaCode()).eq(UserInfo::getPhone, param.getPhone()).count();
		if(count > 0) {
			return Response.fail("该手机号已被注册");
		}
		if(StringUtil.isEmpty(param.getInvitationCode())) {
			return Response.fail("请输入邀请码");
		}
		AgentInvitationCode agentInvitationCode = agentInvitationCodeService.lambdaQuery().eq(AgentInvitationCode::getInvitationCode, param.getInvitationCode()).one();
		if(agentInvitationCode == null) {
			return Response.fail("邀请码不正确，请重新输入");
		}
		if (!StringUtil.isEmpty(param.getEmail())) {
			if (!StringUtil.isEmail(param.getEmail())) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			int emailCount =this.lambdaQuery().eq(UserInfo::getEmail, param.getEmail()).count();
			if (emailCount > 0) {
				return Response.fail("该邮箱已被注册");
			}
		}
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<Void> register(RegisterParamVO param, String ip) {
		if (!registerVerify(param).getCode().equals(Response.success().getCode())){
			return registerVerify(param);
		}
		if(StringUtil.isEmpty(param.getLoginPwd())) {
			return Response.fail("请输入登录密码");
		}
		if(param.getLoginPwd().length() < 8 || param.getLoginPwd().length() > 12 || !StringUtil.isNumberAndEnglish(param.getLoginPwd())) {
			return Response.fail("密码长度为8-12个字符，且必须包含字母和数字");
		}
		if (StringUtil.isEmpty(param.getRealName())) {
			return Response.fail("请输入真实姓名");
		}
		if (StringUtil.isEmpty(param.getCertificateNumber())) {
			return Response.fail("请输入真实身份证号");
		}
		Country country = countryService.getCountryByAreaCode(param.getAreaCode());
		AgentInvitationCode agentInvitationCode = agentInvitationCodeService.lambdaQuery().eq(AgentInvitationCode::getInvitationCode, param.getInvitationCode()).one();
		AgentInfo agent = null;
		agentInvitationCode.setSpreadCount(agentInvitationCode.getSpreadCount() + 1);
		agentInvitationCode.updateById();
		int agentId = agentInvitationCode.getAgentId();
		agent = agentInfoService.getById(agentId);
		int generalAgentId = agent.getGeneralParentId() == 0 ? agentId : agent.getGeneralParentId();
		Date now = new Date();
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		UserInfo newUser = new UserInfo();
		newUser.setAccountType(Constant.ACCOUNT_TYPE_REAL);
		newUser.setNickname(StringUtil.newUserNickname());
		newUser.setAreaCode(param.getAreaCode());
		newUser.setPhone(param.getPhone());
		newUser.setEmail(param.getEmail());
		newUser.setAgentId(agentId);
		newUser.setGeneralAgentId(generalAgentId);
		newUser.setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, param.getLoginPwd()));
		newUser.setRegion(country.getNameCn());
		newUser.setRegTime(now);
		newUser.setRegIp(ip);
		newUser.setRegAddress(ia.getAddress2());
		newUser.setRegInvitationCode(param.getInvitationCode());
		newUser.setRealName(param.getRealName());
		newUser.setCertificateNumber(param.getCertificateNumber());
		newUser.insert();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(newUser.getId());
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.USER_REGISTER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.USER_REGISTER.getName());
		StringBuilder newContent = new StringBuilder();
		newContent.append("登录ID：").append(newUser.getId());
		newContent.append("\n昵称：").append(newUser.getNickname());
		newContent.append("\n手机号：").append(newUser.getAreaCode()).append(newUser.getPhone());
		if(agent != null) {
			newContent.append("\n所属代理：").append(agent.getAgentName()).append("(").append(agentId).append(")");
		}
		if(!StringUtil.isEmpty(param.getEmail())) {
			newContent.append("\n邮箱：").append(param.getEmail());
		}
		if(!StringUtil.isEmpty(param.getInvitationCode())) {
			newContent.append("\n邀请码：").append(param.getInvitationCode());
		}
		if(!StringUtil.isEmpty(param.getRealName())) {
			newContent.append("\n真实姓名：").append(param.getRealName());
		}
		if(!StringUtil.isEmpty(param.getCertificateNumber())) {
			newContent.append("\n真实证件号：").append(param.getCertificateNumber());
		}
		udcr.setNewContent(newContent.toString());
		udcr.setIp(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(newUser.getOperator());
		udcr.insert();
		if(agentId > 0) {
			if(newUser.getAgentId().equals(newUser.getGeneralAgentId())) {
				AgentMemberInfo um = new AgentMemberInfo();
				um.setAgentId(newUser.getAgentId());
				um.setMemberUserId(newUser.getId());
				um.setMemberAgentId(newUser.getAgentId());
				um.insert();
			} else {
		        List<ChildAndParentVO> uaList = this.pNextParentUsers(newUser.getId());
				for(ChildAndParentVO ua : uaList) {
					AgentMemberInfo um = new AgentMemberInfo();
					um.setAgentId(ua.getChildId());
					um.setMemberUserId(newUser.getId());
					um.setMemberAgentId(newUser.getAgentId());
					um.insert();
				}
			}
		}
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<TokenUserVO> login(String areaCode, String loginAccount, String loginPwd, int loginType, String ip, String requestUrl) {
		if(StringUtil.isEmpty(loginAccount)) {
			return Response.fail("请输入您的账号");
		}
		if(StringUtil.isEmpty(loginPwd)) {
			return Response.fail("请输入登录密码");
		}
		
		LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
		switch(loginType) {
		case 1:
			if(StringUtil.isEmpty(areaCode)) {
				return Response.fail("请选择地区编号");
			}
			if(!StringUtil.isNumber(loginAccount)) {
				return Response.fail("手机号码格式错误，请重新输入");
			}
			lqw.eq(UserInfo::getAreaCode, areaCode);
			lqw.eq(UserInfo::getPhone, loginAccount);
			break;
		case 2:
			if(!StringUtil.isEmail(loginAccount)) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			lqw.eq(UserInfo::getEmail, loginAccount);
			break;
		default:
			if(!StringUtil.isNumber(loginAccount)) {
				return Response.fail("登录id的格式错误，请重新输入");
			}
			lqw.eq(UserInfo::getId, loginAccount);
			break;
		}
		UserInfo me = this.getOne(lqw);
		if(me == null) {
			return Response.fail("账号或者密码错误");
		} 
		
		String key = RedisKeyPrefix.getUserLoginFailedTimesKey(me.getId());
		UserLoginFailedTimesVO  ulf = redisDao.getBean(RedisDbTypeEnum.DEFAULT, key, UserLoginFailedTimesVO.class);
		
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		
		if(ulf != null && spc.getMaxTimesOfIncoreectPassword() > 0 && ulf.getTimes() >= spc.getMaxTimesOfIncoreectPassword()) {
  			return Response.fail("账号或密码连续错误" + ulf.getTimes() + "次，需24小时后才能使用账户密码登录");
		}
		
		
		IpAddress ia = ipAddressService.getIpAddress(ip);
		String ipAddress = ia.getAddress2();
		Date now = new Date();
		loginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, loginPwd);
		if(!me.getLoginPwd().equals(loginPwd)) {
			if(ulf == null) {
				ulf = new UserLoginFailedTimesVO();
			}
			ulf.setTimes(ulf.getTimes() + 1);
			redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, ulf, 24, TimeUnit.HOURS);
			//登录日志
			UserLoginLog ull = new UserLoginLog();
			ull.setUserId(me.getId());
			ull.setOperateStatus(1);
			ull.setOperateTime(now);
			ull.setIp(ip);
			ull.setIpAddress(ipAddress);
			ull.setRequestUrl(requestUrl);
			ull.insert();
			if(spc.getMaxTimesOfIncoreectPassword() > 0) {
				if (ulf.getTimes() >= spc.getMaxTimesOfIncoreectPassword()) {
					return Response.fail("账号或密码连续错误" + ulf.getTimes() + "次，需24小时后才能使用账户密码登录");
				}
				return Response.fail("账号或密码错误，" + (spc.getMaxTimesOfIncoreectPassword() - ulf.getTimes()) + "次后该账号将被锁定");
				
			}
			return Response.fail("账号或密码错误");
		}
		if(!me.getLoginEnable()) {
			//登录日志
			UserLoginLog ull = new UserLoginLog();
			ull.setUserId(me.getId());
			ull.setOperateStatus(2);
			ull.setOperateTime(now);
			ull.setIp(ip);
			ull.setIpAddress(ipAddress);
			ull.setRequestUrl(requestUrl);
			ull.insert();
			return Response.fail("您的账户已被限制登录，详情原因请咨询客服");
		} 
		redisDao.del(RedisDbTypeEnum.DEFAULT, RedisKeyPrefix.getUserLoginFailedTimesKey(me.getId()));
		String token = PasswordGenerator.createToken(me.getId(), System.currentTimeMillis());
		String tokenKey = RedisKeyPrefix.getTokenUserKey(token);
		TokenUserVO tokenUser = new TokenUserVO();
		tokenUser.setLoginId(me.getId());
		tokenUser.setAccountType(me.getAccountType());
		tokenUser.setToken(token);
		redisDao.setBean(RedisDbTypeEnum.DEFAULT, tokenKey, tokenUser, 30, TimeUnit.MINUTES);
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(me.getId());
		OnlineUserVO ou = redisDao.getBean(RedisDbTypeEnum.DEFAULT, onlineUserKey, OnlineUserVO.class);
		if (ou == null) {
			ou = new OnlineUserVO();
		}
		ou.setLatestToken(token);
		ou.setCurrentTimestamp(System.currentTimeMillis());
		ou.getTokenList().add(token);
		redisDao.setBean(RedisDbTypeEnum.DEFAULT, onlineUserKey, ou, 30, TimeUnit.MINUTES);
		this.lambdaUpdate().set(UserInfo::getLastLoginTime, now)
			.set(UserInfo::getLastLoginIp, ip)
			.set(UserInfo::getLastLoginAddress, ipAddress)
			.eq(UserInfo::getId, me.getId()).update();
		//登录日志
		UserLoginLog ull = new UserLoginLog();
		ull.setUserId(me.getId());
		ull.setOperateTime(now);
		ull.setIp(ip);
		ull.setIpAddress(ipAddress);
		ull.setRequestUrl(requestUrl);
		ull.insert();
		return Response.successData(tokenUser);
	}

	@Override
	public Response<Void> logout(Integer userId, String ip, String requestUrl) {
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(userId);
		OnlineUserVO ou = redisDao.getBean(RedisDbTypeEnum.DEFAULT, onlineUserKey, OnlineUserVO.class);
		if (ou != null) {
			String token = ou.getLatestToken();
			String tokenKey = RedisKeyPrefix.getTokenUserKey(token);
			redisDao.del(RedisDbTypeEnum.DEFAULT, tokenKey);
			redisDao.del(RedisDbTypeEnum.DEFAULT, onlineUserKey);
		}
		IpAddress ia = ipAddressService.getIpAddress(ip);
		String ipAddress = ia.getAddress2();
		UserLoginLog ull = new UserLoginLog();
		ull.setUserId(userId);
		ull.setLoginType(1);
		ull.setOperateStatus(0);
		ull.setOperateTime(new Date());
		ull.setIp(ip);
		ull.setIpAddress(ipAddress);
		ull.setRequestUrl(requestUrl);
		ull.insert();
		return Response.success();
	}
	
	@Override
	public void managerUserList(Page<UserInfo> page, UserListSearchParamVO vo) {
		Set<Integer> ids = new HashSet<>();
		if(vo.getAgentId() != null && vo.getAgentId() > 0 && vo.isSearchChildOfAgent()) {
			List<ChildAndParentVO> auLst = this.agentMemberInfoMapper.agentMemberInfoList(vo.getAgentId());
			auLst.forEach(a->{
				ids.add(a.getChildId());
			});
		}
		userInfoMapper.managerUserList(page, vo, ids);
		List<UserInfo> list = page.getRecords();
		for(UserInfo i : list) {
			if(i.getUnavailableWithdrawalAmt().compareTo(i.getAvailableAmt()) == 1) {
				i.setUnavailableWithdrawalAmt(i.getAvailableAmt());
			}
			String key = RedisKeyPrefix.getOnlineUserKey(i.getId());
			OnlineUserVO onlineUser = redisDao.getBean(RedisDbTypeEnum.DEFAULT, key, OnlineUserVO.class);
			if(onlineUser != null) {
				i.setIsOnline(true);
			}
		}
	}

	@Transactional
	@Override
	public Response<Void> managerUserAdd(AddUserParamVO vo, String ip,  String operator) {
		Integer generalAgentId = 0;
		Integer agentId = vo.getAgentId() == null ? 0 : vo.getAgentId();
		AgentInfo agent = null;
		if(agentId == null || agentId == 0) {
			return Response.fail("请选择上级代理");
		}
		agent = agentInfoService.lambdaQuery().eq(AgentInfo::getId, agentId).one();
		if(agent == null) {
			return Response.fail("代理不存在，请重新选择");
		}
		generalAgentId = agent.getGeneralParentId() == 0 ? agentId : agent.getGeneralParentId();
		if(vo.getAccountType() != Constant.ACCOUNT_TYPE_REAL && vo.getAccountType() != Constant.ACCOUNT_TYPE_VISUAL) {
			return Response.fail("请选择账号类型");
		}
		if (vo.getAccountType() == Constant.ACCOUNT_TYPE_REAL) {//实盘账户
			if(StringUtil.isEmpty(vo.getRealName()) || StringUtil.isEmpty(vo.getCertificateNumber())) {
				return Response.fail("实盘账户,必须填写真实姓名，真实证件号");
			}
		}
		if(StringUtil.isEmpty(vo.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(vo.getPhone())) {
			return Response.fail("请输入手机号");
		}
		if(!StringUtil.isNumber(vo.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(vo.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		Country country = countryService.getCountryByAreaCode(vo.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		if(this.lambdaQuery().eq(UserInfo::getAreaCode, vo.getAreaCode()).eq(UserInfo::getPhone, vo.getPhone()).count() > 0) {
			return Response.fail("此手机号已被注册，请重新输入");
		}
		if(StringUtil.isEmpty(vo.getLoginPwd())) {
			return Response.fail("请输入登录密码");
		}
		if(!StringUtil.isEmpty(vo.getEmail())) {
			if(!StringUtil.isEmail(vo.getEmail())) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).count() > 0) {
				return Response.fail("此邮箱账号已被注册，请重新输入");
			}
		}
		UserInfo newUser = new UserInfo();
		Date now = new Date();
		IpAddress ia = this.ipAddressService.getIpAddress(ip);
		newUser.setAccountType(vo.getAccountType());
		newUser.setAgentId(agentId);
		newUser.setGeneralAgentId(generalAgentId);
		newUser.setNickname(StringUtil.newUserNickname());
		newUser.setAreaCode(vo.getAreaCode());
		newUser.setPhone(vo.getPhone());
		newUser.setEmail(vo.getEmail());
		newUser.setAgentId(agentId);
		newUser.setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getLoginPwd()));
		newUser.setRegion(country.getNameCn());
		newUser.setRegTime(now);
		newUser.setRegIp(ip);
		newUser.setRegAddress(ia.getAddress2());
		if(!StringUtil.isEmpty(vo.getRealName()) ) {
			newUser.setRealName(vo.getRealName());
		}
		if(!StringUtil.isEmpty(vo.getCertificateNumber())) {
			newUser.setCertificateNumber(vo.getCertificateNumber());
		}
		newUser.insert();
		//模拟账户,默认处理属性
		if(newUser.getAccountType() == Constant.ACCOUNT_TYPE_VISUAL) {
			UserInfo visualUserInfo = new UserInfo();
			visualUserInfo.setId(newUser.getId());
			visualUserInfo.setRealName("模拟用户" + newUser.getId());
			visualUserInfo.setCertificateNumber("无");
			visualUserInfo.setRealAuthStatus(UserRealAuthStatusEnum.REVIEWED.getCode());
			visualUserInfo.setFundingStatus(UserFundingStatusEnum.REVIEWED.getCode());
			visualUserInfo.setTradeEnable(true);
			visualUserInfo.updateById();
		}
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(newUser.getId());
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.ADD_USER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.ADD_USER.getName());
		StringBuilder newContent = new StringBuilder();
		newContent.append("登录ID：").append(newUser.getId());
		newContent.append("\n昵称：").append(newUser.getNickname());
		newContent.append("\n手机号：").append(newUser.getAreaCode()).append(newUser.getPhone());
		if(agent != null) {
			newContent.append("\n所属代理：").append(agent.getAgentName()).append("(").append(agentId).append(")");
		}
		if(!StringUtil.isEmpty(vo.getEmail())) {
			newContent.append("\n邮箱：").append(vo.getEmail());
		}
		udcr.setNewContent(newContent.toString());
		udcr.setIp(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		if(agentId > 0) {
			if(newUser.getAgentId().equals(newUser.getGeneralAgentId())) {
				AgentMemberInfo um = new AgentMemberInfo();
				um.setAgentId(newUser.getAgentId());
				um.setMemberUserId(newUser.getId());
				um.setMemberAgentId(newUser.getAgentId());
				um.insert();
			} else {
		        List<ChildAndParentVO> uaList = this.pNextParentUsers(newUser.getId());
				for(ChildAndParentVO ua : uaList) {
					AgentMemberInfo um = new AgentMemberInfo();
					um.setAgentId(ua.getChildId());
					um.setMemberUserId(newUser.getId());
					um.setMemberAgentId(newUser.getAgentId());
					um.insert();
				}
			}
		}
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> managerUserEdit(EditUserParamVO vo, String ip, String operator) {
		UserInfo old = this.getById(vo.getUserId());
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		if(vo.getAgentId() == null || vo.getAgentId() == 0) {
			return Response.fail("请选择上级代理");
		}
		if(StringUtil.isEmpty(vo.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(vo.getPhone())) {
			return Response.fail("请输入手机号");
		}
		if(!StringUtil.isNumber(vo.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(vo.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		Country country = countryService.getCountryByAreaCode(vo.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		StringBuilder oldContent = new StringBuilder();
		StringBuilder newContent = new StringBuilder();
		Integer newGeneralAgentId = 0;
		boolean changedAgent = false;
		LambdaUpdateWrapper<UserInfo> luw = new LambdaUpdateWrapper<>();
		luw.eq(UserInfo::getId, vo.getUserId());
		if(old.getAgentId() == 0) {
			AgentInfo newAgent = agentInfoService.getById(vo.getAgentId());
			if(newAgent == null) {
				return Response.fail("代理信息错误");
			}
			oldContent.append("所属代理：无");
			newContent.append("所属代理：").append(newAgent.getAgentName()).append("(").append(newAgent.getId()).append(")");
			luw.set(UserInfo::getAgentId, vo.getAgentId());
			newGeneralAgentId = newAgent.getGeneralParentId() == 0 ? newAgent.getId() : newAgent.getGeneralParentId();
			luw.set(UserInfo::getGeneralAgentId, newGeneralAgentId);
			changedAgent = true;
		} else {
			if(!vo.getAgentId().equals(old.getAgentId())){
				AgentInfo oldAgent = agentInfoService.getById(old.getAgentId());
				AgentInfo newAgent = agentInfoService.getById(vo.getAgentId());
				if(newAgent == null) {
					return Response.fail("代理信息错误");
				}
				oldContent.append("所属代理：").append(oldAgent.getAgentName()).append("(").append(oldAgent.getId()).append(")");
				newContent.append("所属代理：").append(newAgent.getAgentName()).append("(").append(newAgent.getId()).append(")");
				luw.set(UserInfo::getAgentId, vo.getAgentId());
				newGeneralAgentId = newAgent.getGeneralParentId() == 0 ? newAgent.getId() : newAgent.getGeneralParentId();
				luw.set(UserInfo::getGeneralAgentId, newGeneralAgentId);
				changedAgent = true;
			}
		}
		if(!old.getAreaCode().equals(vo.getAreaCode()) || !old.getPhone().equals(vo.getPhone())) {
			if(this.lambdaQuery().eq(UserInfo::getAreaCode, vo.getAreaCode()).eq(UserInfo::getPhone, vo.getPhone()).ne(UserInfo::getId, old.getId()).count() > 0) {
				return Response.fail("此手机号已被注册，请重新输入");
			}
			oldContent.append("\n手机号码：").append(old.getAreaCode()).append(old.getPhone());
			newContent.append("\n手机号码：").append(vo.getAreaCode()).append(vo.getPhone());
			luw.set(UserInfo::getAreaCode, vo.getAreaCode());
			luw.set(UserInfo::getPhone, vo.getPhone());
		}
		if(StringUtil.isEmpty(old.getEmail())) {
			if(!StringUtil.isEmpty(vo.getEmail())) {
				if(!StringUtil.isEmail(vo.getEmail())) {
					return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
				}
				if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).ne(UserInfo::getId, old.getId()).count() > 0) {
					return Response.fail("此邮箱账号已被注册，请重新输入");
				}
				oldContent.append("\n邮箱：无");
				newContent.append("\n邮箱：").append(vo.getEmail());
				luw.set(UserInfo::getEmail, vo.getEmail());
			}
		} else {
			if(StringUtil.isEmpty(vo.getEmail())) {
				oldContent.append("\n邮箱：").append(old.getEmail());
				newContent.append("\n邮箱：无");
				luw.set(UserInfo::getEmail, "");
			} else if(!old.getEmail().equals(vo.getEmail())) {
				if(!StringUtil.isEmail(vo.getEmail())) {
					return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
				}
				if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).count() > 0) {
					return Response.fail("此邮箱账号已被注册，请重新输入");
				}
				oldContent.append("\n邮箱：").append(old.getEmail());
				newContent.append("\n邮箱：").append(vo.getEmail());
				luw.set(UserInfo::getEmail, vo.getEmail());
			}
		}
		
		if(!StringUtil.isEmpty(vo.getLoginPwd())) {
			String newloginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getLoginPwd());
			oldContent.append("\n登录密码：").append("******");
			newContent.append("\n登录密码：").append("******");
			luw.set(UserInfo::getLoginPwd, newloginPwd);
		}if(!StringUtil.isEmpty(vo.getFundPwd())) {
			String newFundPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getFundPwd());
			oldContent.append("\n交易密码：").append("******");
			newContent.append("\n交易密码：").append("******");
			luw.set(UserInfo::getFundPwd, newFundPwd);
		}
		if(vo.getLoginEnable() != null && vo.getLoginEnable() != old.getLoginEnable()) {
			oldContent.append("\n登录状态：").append(old.getLoginEnable() ? "可登录" : "不可登录");
			newContent.append("\n登录状态：").append(vo.getLoginEnable() ? "可登录" : "不可登录");
			luw.set(UserInfo::getLoginEnable, vo.getLoginEnable());
		}
		if(vo.getTradeEnable() != null && vo.getTradeEnable() != old.getTradeEnable()) {
			oldContent.append("\n交易状态：").append(old.getTradeEnable() ? "可交易" : "不可交易");
			newContent.append("\n交易状态：").append(vo.getTradeEnable() ? "不可交易" : "不可交易");
			luw.set(UserInfo::getTradeEnable, vo.getTradeEnable());
		}
		if(!StringUtil.isEmpty(luw.getSqlSet())) {
			this.update(luw);
			if(changedAgent) {
				LambdaUpdateWrapper<AgentMemberInfo> delWrap = new LambdaUpdateWrapper<>();
				delWrap.eq(AgentMemberInfo::getMemberUserId, vo.getUserId());
				agentMemberInfoMapper.delete(delWrap);
				if(vo.getAgentId() > 0) {
					AgentMemberInfo um = new AgentMemberInfo();
					um.setAgentId(vo.getAgentId());
					um.setMemberUserId(vo.getUserId());
					um.setMemberAgentId(vo.getAgentId());
					um.insert();
					if(!vo.getAgentId().equals(newGeneralAgentId)) {
						List<ChildAndParentVO> uaList = this.pNextParentAgents(vo.getAgentId());
						for(ChildAndParentVO ua : uaList) {
							um = new AgentMemberInfo();
							um.setAgentId(ua.getChildId());
							um.setMemberUserId(vo.getUserId());
							um.setMemberAgentId(vo.getAgentId());
							um.insert();
						}
					}
				}
			}
			UserDataChangeRecord udcr = new UserDataChangeRecord();
			udcr.setUserId(vo.getUserId());
			udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
			udcr.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
			udcr.setOldContent(oldContent.toString());
			udcr.setNewContent(newContent.toString());
			udcr.setIp(ip);
			IpAddress ia = ipAddressService.getIpAddress(ip);
			udcr.setIpAddress(ia.getAddress2());
			udcr.setOperator(operator);
			udcr.insert();
		}
		return Response.success();
	}

	@Override
	public Response<Void> forcedOffline(Integer userId) {
		UserInfo userInfo = this.getById(userId);
		if(userInfo == null) {
			return Response.fail("用户信息错误");
		}
		//踢用户下线
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(userId);
		OnlineUserVO onlineUser = redisDao.getBean(RedisDbTypeEnum.DEFAULT, onlineUserKey, OnlineUserVO.class);
		if(onlineUser != null) {
			redisDao.del(RedisDbTypeEnum.DEFAULT, onlineUserKey);
			redisDao.del(RedisDbTypeEnum.DEFAULT, RedisKeyPrefix.getTokenUserKey(onlineUser.getLatestToken()));
			for(String token : onlineUser.getTokenList()) {
				redisDao.del(RedisDbTypeEnum.DEFAULT, RedisKeyPrefix.getTokenUserKey(token));
			}
		}
		return Response.success();
	}

	@Override
	public Response<AssetsDetailVO> assetsDetail(Integer userId) {
		AssetsDetailVO vo = new AssetsDetailVO();
		UserInfo ui = this.getById(userId);
		vo.setAvailableAmt(ui.getAvailableAmt());
		vo.setDetentionAmt(ui.getDetentionAmt());
		vo.setIpoAmt(ui.getIpoAmt());
		vo.setTradingFrozenAmt(ui.getTradingFrozenAmt());
		vo.setAvailableWithdrawalAmt(ui.getAvailableAmt());
		BigDecimal totalAssets = ui.getAvailableAmt().subtract(ui.getDetentionAmt()).add(ui.getIpoAmt()).add(ui.getTradingFrozenAmt());
		BigDecimal totalMarketValue = BigDecimal.ZERO; 
		BigDecimal totalProfit = BigDecimal.ZERO; 
		BigDecimal todayProfit = BigDecimal.ZERO; 
		BigDecimal totalHoldingValue = BigDecimal.ZERO; //总成本
		LambdaQueryWrapper<UserStockPosition> lqw = new LambdaQueryWrapper<>();
		lqw.select(UserStockPosition::getStockCode, UserStockPosition::getStockType, UserStockPosition::getBuyingPrice, UserStockPosition::getBuyingShares, UserStockPosition::getPositionDirection);
		lqw.eq(UserStockPosition::getUserId, userId);
		lqw.eq(UserStockPosition::getPositionStatus, 2);
		List<UserStockPosition> list = this.userStockPositionService.list(lqw);
		if(list.size() > 0) {
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			for(UserStockPosition i : list) {
				if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
					stockGids.add(i.getStockType() + i.getStockCode());
				} else {
					hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
				}
			}
			if(stockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
				 for(StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					 for(UserStockPosition i : list) { 
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							 BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());//持有股数
							 BigDecimal holdingValue = i.getBuyingPrice().multiply(buyingShares);//持有成本=买入价格*持有股数
							 BigDecimal marketValue = stockQuotesVO.getNowPrice().multiply(buyingShares);//持有市值=当前价格*持有股数
							 totalMarketValue = totalMarketValue.add(marketValue);//持仓总市值
							 totalHoldingValue = totalHoldingValue.add(holdingValue);//持仓总总成本
							 if(i.getPositionDirection() == Constant.GO_LONG) {
								 totalProfit = totalProfit.add(marketValue.subtract(holdingValue));
								 todayProfit = todayProfit.add(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							 } else {
								 totalProfit = totalProfit.add(holdingValue.subtract(marketValue));
								 todayProfit = todayProfit.subtract(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							 }
						 }
					 }
				 }
			}
			if (hkOrUsStockGids.size() > 0) {
				BigDecimal hkExchangeRate = null, usExchangeRate = null;
				List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
				for (StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					for (UserStockPosition i : list) {
						if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());//持有股数
							BigDecimal holdingValue = i.getBuyingPrice().multiply(buyingShares);//持有成本=买入价格*持有股数
							BigDecimal marketValue = stockQuotesVO.getNowPrice().multiply(buyingShares);//持有市值=当前价格*持有股数
							if (i.getStockType().equals(StockTypeEnum.US.getCode())) {//美股利率计算
								if (usExchangeRate == null) {
									usExchangeRate = sysParamConfigService.getExchangeRate(CurrencyEnum.USD);//美股利率
								}
								marketValue = marketValue.divide(usExchangeRate, 2, RoundingMode.HALF_UP);//美股持有市值
								holdingValue = holdingValue.divide(usExchangeRate, 2, RoundingMode.HALF_UP);//美股持有成本
							} else {//港股利率计算
								if (hkExchangeRate == null) {
									hkExchangeRate = sysParamConfigService.getExchangeRate(CurrencyEnum.HKD);//港股利率
								}
								marketValue = marketValue.divide(hkExchangeRate, 2, RoundingMode.HALF_UP);//港股持有市值
								holdingValue = holdingValue.divide(hkExchangeRate, 2, RoundingMode.HALF_UP);//港股只有成本
							}
							totalMarketValue = totalMarketValue.add(marketValue);//持仓总市值
							totalHoldingValue = totalHoldingValue.add(holdingValue);//持仓总成本
							if (i.getPositionDirection() == Constant.GO_LONG) {
								totalProfit = totalProfit.add(marketValue.subtract(holdingValue));
								todayProfit = todayProfit.add(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							} else {
								totalProfit = totalProfit.add(holdingValue.subtract(marketValue));
								todayProfit = todayProfit.subtract(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							}
						}
					}
				}
			}
			totalAssets = totalAssets.add(totalMarketValue);
		}
		vo.setTotalAssets(totalAssets);
		vo.setTotalMarketValue(totalMarketValue);
		vo.setTotalProfit(totalProfit);
		vo.setTodayProfit(todayProfit);
		BigDecimal todayProfitRate = BigDecimal.ZERO;
		try {
			todayProfitRate = todayProfit.divide(totalHoldingValue, 2, RoundingMode.HALF_UP);////当日盈亏/持仓总成本
		} catch (Exception e) {
			todayProfitRate = BigDecimal.ZERO;
		}
		vo.setTodayProfitRate(todayProfitRate);//当日盈亏/持仓总成本
		//增加持仓、挂单、新股、平仓，总数数据
		vo.setUserStockPositionTotal(userStockPositionService.lambdaQuery().eq(UserStockPosition::getUserId, userId).eq(UserStockPosition::getPositionStatus, 2).count());
		vo.setUserStockPendingTotal(userStockPendingService.lambdaQuery().eq(UserStockPending::getUserId, userId).eq(UserStockPending::getPositionStatus, 1).count());
		vo.setUserNewShareSubscriptionTotal(userNewShareSubscriptionService.lambdaQuery().eq(UserNewShareSubscription::getUserId, userId).count());
		vo.setUserStockClosingPositionTotal(userStockClosingPositionService.lambdaQuery().eq(UserStockClosingPosition::getUserId, userId).count());
		QueryWrapper<UserStockClosingPosition> qw = new QueryWrapper<>();
		qw.select("ifnull(sum(actual_profit),0) as actual_profit");
		qw.eq("user_id", userId);
		Map<String, Object> map = userStockClosingPositionService.getMap(qw);
		BigDecimal actualProfitSum = (BigDecimal) map.get("actual_profit");
		vo.setAggregateTotalProfit(actualProfitSum.subtract(totalHoldingValue));//平仓总盈亏-持仓总成本
		return Response.successData(vo);
	}

	@Override
	@Transactional
	public Response<Void> modifyPhone(Integer userId, String areaCode, String phone, String ip, String operator) {
		if(StringUtil.isEmpty(areaCode)) {
			return Response.fail("请选择地区编号");
		}
		Country country = countryService.getCountryByAreaCode(areaCode);
		if(country == null) {
			return Response.fail("地区编号错误");
		}
		if(StringUtil.isEmpty(phone)) {
			return Response.fail("请输入手机号码");
		}
		if(!StringUtil.isNumber(phone)) {
			return Response.fail("手机号格式错误，请重新输入");
		}
		if(this.lambdaQuery().eq(UserInfo::getAreaCode, areaCode).eq(UserInfo::getPhone, phone).ne(UserInfo::getId, userId).count() > 0) {
			return Response.fail("此手机号已被注册，请重新输入");
		}
		UserInfo ui = this.getById(userId);
		String oldMobilePhone = ui.getAreaCode() + ui.getPhone();
		String newMobilePhone = areaCode + phone;
		if(oldMobilePhone.equals(newMobilePhone)) {
			return Response.fail("请输入新的手机号码");
		}
		this.lambdaUpdate().set(UserInfo::getAreaCode, areaCode).set(UserInfo::getPhone, phone).eq(UserInfo::getId, userId).update();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(userId);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
		udcr.setOldContent("手机号码：" + oldMobilePhone);
		udcr.setNewContent("手机号码：" + newMobilePhone);
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> modifyEmail(Integer userId, String emmail, String ip, String operator) {
		if(!StringUtil.isEmail(emmail)) {
			return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
		}
		if(this.lambdaQuery().eq(UserInfo::getEmail, emmail).ne(UserInfo::getId, userId).count() > 0) {
			return Response.fail("此邮箱账号已被注册，请重新输入");
		}
		this.lambdaUpdate().set(UserInfo::getEmail, emmail).eq(UserInfo::getId, userId).update();
		UserInfo ui = this.getById(userId);
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(userId);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
		udcr.setOldContent("邮箱：" + (StringUtil.isEmpty(ui.getEmail()) ? "" : ui.getEmail()));
		udcr.setNewContent("邮箱：" + emmail);
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> modifyLoginPwd(Integer userId, String oldLoginPwd, String newLoginPwd, String ip,
			String operator) {
		UserInfo ui = this.getById(userId);
		oldLoginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, oldLoginPwd);
		if(!ui.getLoginPwd().equals(oldLoginPwd)) {
			return Response.fail("原登录密码验证失败，请重新输入");
		}
		return doModifyLoginPwd(userId, newLoginPwd, ip, operator);
	}

	@Override
	@Transactional
	public Response<Void> modifyFundPwd(Integer userId, String oldPwd, String newFundPwd, String ip, String operator) {
		UserInfo ui = this.getById(userId);
		String oldFundPwd = ui.getFundPwd();
		oldPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, oldPwd);
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		if(StringUtil.isEmpty(oldFundPwd)) {
			if(!oldPwd.equals(ui.getLoginPwd())) {
				return Response.fail("原登录密码验证失败，请重新输入");
			}
			udcr.setOldContent("交易密码：");
		} else {
			if(!oldPwd.equals(ui.getFundPwd())) {
				return Response.fail("原交易密码验证失败，请重新输入");
			}
			udcr.setOldContent("交易密码：*******");
		}
		new UserInfo().setId(userId).setFundPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, newFundPwd)).updateById();
		udcr.setUserId(userId);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
		udcr.setNewContent("交易密码：******");
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}

	@Override
	public Response<Void> pwdVerify(Integer userId, String pwd, int pwdType) {
		// TODO Auto-generated method stub
		UserInfo ui = this.getById(userId);
		pwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, pwd);
		switch (pwdType) {
		case 1: //交易密码
			if(StringUtil.isEmpty(ui.getFundPwd())) {
				return Response.fail("交易密码未设置，请验证登录密码");
			} else {
				if(!pwd.equals(ui.getFundPwd())) {
					return Response.fail("交易密码验证失败，请重新输入");
				}
			}
			break;

		default: //登录密码
			if(!pwd.equals(ui.getLoginPwd())) {
				return Response.fail("登录密码验证失败，请重新输入");
			}
			break;
		}
		return Response.success();
	}

	/**
	 * 阿里云-发起人脸认证
	 */
	@Override
	public Response<ResultObject> doAliyunInitFaceVerify(AliyunInitFaceVerifyParamVO param) {
		String certName = "";//真实姓名
		String certNo ="";//真实证件号
		if (param.getVerifyType() == 0) {//注册人脸认证(未注册用户，注册+人脸认证)
			if (param.getRegisterParamVO() == null) {
				return Response.fail("registerParamVO:注册参数，不能为空");
			}
			if (StringUtil.isEmpty(param.getRegisterParamVO().getRealName())) {
				return Response.fail("registerParamVO:注册参数-真实姓名，不能为空");
			}
			if (StringUtil.isEmpty(param.getRegisterParamVO().getCertificateNumber())) {
				return Response.fail("registerParamVO:注册参数-证件号，不能为空");
			}
			certName = param.getRegisterParamVO().getRealName();
			certNo = param.getRegisterParamVO().getCertificateNumber();
		}else {//其他人脸认证，必须是注册用户
			if (param.getUserInfo() == null) {
				return Response.fail("用户未注册，请先注册"); 
			}
			if (!param.getUserInfo().isFaceVerify()) {
				return Response.fail("该用户未进行过人脸认证"); 
			}
			certName = param.getUserInfo().getRealName();
			certNo = param.getUserInfo().getCertificateNumber();
		}
		//获取-金融级人脸验证方案(【身份证、姓名】检测认证)-请求参数
		InitFaceVerifyRequest initFaceVerifyRequestIdpro = InitFaceVerify.getInitFaceVerifyRequestIdpro(certName, certNo, param.getReturnUrl(), param.getMetaInfo());
		//执行-金融级活体检测-发起认证请求
		try {
			InitFaceVerifyResponse initFaceVerifyResponse = InitFaceVerify.doInitFaceVerify(initFaceVerifyRequestIdpro);
			if (initFaceVerifyResponse.getBody().getCode().equals("200") && 
					initFaceVerifyResponse.getBody().getResultObject().getCertifyId() != null) {
				//存入人脸认证缓存
				String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(initFaceVerifyResponse.getBody().getResultObject().getCertifyId());
				redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, param, 5, TimeUnit.MINUTES);//5分钟销毁
				return Response.successData(initFaceVerifyResponse.getBody().getResultObject());
			}else {
				return Response.fail(JSONUtil.toJsonStr(initFaceVerifyResponse.getBody().getMessage()));
			}
		} catch (Exception e) {
			log.error("阿里云-发起人脸认证-失败:"+ e);
			return Response.fail("阿里云-发起人脸认证-失败,请联系开发人员");
		}
	}

	/**
	 * 阿里云-获取人脸认证结果
	 */
	@Override
	public Response<String> doAliyunDescribeFaceVerify(String certifyId) {
		try {
			DescribeFaceVerifyResponse describeFaceVerifyResponse = DescribeFaceVerify.doDescribeFaceVerify(certifyId);
			if (describeFaceVerifyResponse.getBody().getCode().equals("200") &&
					describeFaceVerifyResponse.getBody().getResultObject().getSubCode() != null ) {
				return doFaceVerifyBusiness(certifyId,describeFaceVerifyResponse.getBody().getResultObject().getSubCode());
			}else {
				return Response.fail(JSONUtil.toJsonStr(describeFaceVerifyResponse.getBody().getMessage()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("阿里云-获取人脸认证结果-失败:{}", e);
			return Response.fail("阿里云-获取人脸认证结果-失败,请联系开发人员");
		}
	}
	
	/**
	 * 执行-人脸认证业务
	 * @throws IOException 
	 */
	@Override
	public Response<String> doFaceVerifyBusiness(String certifyId, String passed) {
		//获取实人认证结果code消息
		String message = AliyunFaceUtil.getDescribeFaceVerifyCodeMessage(passed);
		//获取人脸认证缓存
		String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(certifyId);
		AliyunInitFaceVerifyParamVO aliyunInitFaceVerifyParamVO = redisDao.getBean(RedisDbTypeEnum.DEFAULT, key, AliyunInitFaceVerifyParamVO.class);
		//是否要登录
		boolean loginFalg = false;
		if (aliyunInitFaceVerifyParamVO == null) {
			return Response.fail("人脸认证-已过期");
		}
		//根据人脸认证缓存处理状态处理业务
		switch (aliyunInitFaceVerifyParamVO.getVerifyPassState()) {
		case 0://发起认证的数据
			Long expire = redisDao.getExpire(RedisDbTypeEnum.DEFAULT, key, TimeUnit.MINUTES);//获取人脸缓存时间
			if (passed.equals("200")) {//认证成功
				aliyunInitFaceVerifyParamVO.setVerifyPassState(1);
				//修改缓存
				redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, aliyunInitFaceVerifyParamVO,expire,TimeUnit.MINUTES);
				//处理认证后相关业务
				if(aliyunInitFaceVerifyParamVO.getVerifyType() == 0) {//处理注册
					//执行注册
					Response<Void> ResponseRegister = register(aliyunInitFaceVerifyParamVO.getRegisterParamVO(), aliyunInitFaceVerifyParamVO.getIp());
					if (!ResponseRegister.getCode().equals(Response.success().getCode())){
						return Response.fail(ResponseRegister.getMsg()) ;
					}
					loginFalg = true;//需要登录
				}
			}else {//认证失败
				aliyunInitFaceVerifyParamVO.setVerifyPassState(2);
				//修改缓存
				redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, aliyunInitFaceVerifyParamVO,expire,TimeUnit.MINUTES);
				return Response.fail(message);
			}
			break;
		case 1://认证成功
			if(aliyunInitFaceVerifyParamVO.getVerifyType() == 0) {//处理注册
				loginFalg = true;//需要登录
			}
			break;
		default://认证失败
			return Response.fail(message);
		}
		//处理需要登录业务
		if(loginFalg) {
			//执行登录
			Response<TokenUserVO> ResponseLogin = login(aliyunInitFaceVerifyParamVO.getRegisterParamVO().getAreaCode(), aliyunInitFaceVerifyParamVO.getRegisterParamVO().getPhone(), aliyunInitFaceVerifyParamVO.getRegisterParamVO().getLoginPwd(), 1, aliyunInitFaceVerifyParamVO.getIp(), aliyunInitFaceVerifyParamVO.getRequestUrl());
			if (!ResponseLogin.getCode().equals(Response.success().getCode())){
				return Response.fail(ResponseLogin.getMsg()) ;
			}
			//返回用户token
			String userToken = ResponseLogin.getData().getToken();
			return Response.successData(userToken,userToken);
		}
		return Response.success(Response.success().getMsg());
		
	}
		

	/**
	 * 忘记密码(人脸认证-修改密码)
	 */
	@Override
	public Response<Void> doFaceVerifyModifyLoginPwd(String certifyId,String newLoginPwd,String ip) {
		//获取人脸认证缓存
		String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(certifyId);
		AliyunInitFaceVerifyParamVO aliyunInitFaceVerifyParamVO = redisDao.getBean(RedisDbTypeEnum.DEFAULT, key, AliyunInitFaceVerifyParamVO.class);
		if (aliyunInitFaceVerifyParamVO == null) {
			return Response.fail("人脸认证-已过期");
		}
		switch (aliyunInitFaceVerifyParamVO.getVerifyPassState()) {
		case 0:
			return Response.fail("暂未进行人脸认证");
			
		case 1:
			UserInfo userInfo = aliyunInitFaceVerifyParamVO.getUserInfo();
			return doModifyLoginPwd(userInfo.getId(), newLoginPwd, ip, userInfo.getOperator());	
			
		default:
			return Response.fail("人脸认证失败");
		}
	}
	
	/**
	 * 执行修改密码业务逻辑
	 * @param userId
	 * @param newLoginPwd
	 * @param ip
	 * @param operator
	 * @return
	 */
	public Response<Void> doModifyLoginPwd(Integer userId,String newLoginPwd,String ip,String operator){
		new UserInfo().setId(userId).setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, newLoginPwd)).updateById();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(userId);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
		udcr.setOldContent("登录密码：******");
		udcr.setNewContent("登录密码：******");
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}


}
