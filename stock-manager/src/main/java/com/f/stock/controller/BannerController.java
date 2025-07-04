package com.f.stock.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import config.RedisDbTypeEnum;
import entity.CashinRecord;
import entity.CashoutRecord;
import entity.UserFinancingCertification;
import entity.UserNewShareSubscription;
import entity.UserRealAuthInfo;
import entity.UserStockPending;
import entity.common.Response;
import enums.StockPendingStatusEnum;
import enums.UserFundingStatusEnum;
import enums.UserRealAuthStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import redis.RedisKeyPrefix;
import service.CashinRecordService;
import service.CashoutRecordService;
import service.UserFinancingCertificationService;
import service.UserInfoService;
import service.UserNewShareSubscriptionService;
import service.UserRealAuthInfoService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.RedisDao;
import vo.manager.BannerDataVO;

@RestController
@RequestMapping("/index")
@Api(tags = "首页")
public class BannerController {
	
	@Resource
	private RedisDao redisDao;
	@Resource
	private CashinRecordService cashinRecordService;
	@Resource
	private CashoutRecordService cashoutRecordService;
	@Resource
	private UserStockPositionService userStockPositionService;
	@Resource
	private UserStockPendingService userStockPendingService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private UserNewShareSubscriptionService newShareSubscriptionService;
	@Resource
	private UserRealAuthInfoService userRealAuthInfoService;
	@Resource
	private UserFinancingCertificationService userFinancingCertificationService;
	
	@ApiOperation("顶部横幅数据")
    @PostMapping("/bannerData")
    @ResponseBody
	public Response<BannerDataVO> bannerData() {
		BannerDataVO vo = new BannerDataVO();
		Set<String> keys = redisDao.keys(RedisDbTypeEnum.DEFAULT, RedisKeyPrefix.SERVER + "onlineUser*");
		vo.setOnlineUserCount(keys.size());
		vo.setPendingCashinOrders(cashinRecordService.lambdaQuery().eq(CashinRecord::getOrderStatus, 0).count());
		vo.setPendingCashoutOrders(cashoutRecordService.lambdaQuery().eq(CashoutRecord::getOrderStatus, 0).count());
		vo.setPendingPositionOrders(userStockPendingService.lambdaQuery().eq(UserStockPending::getPositionStatus, StockPendingStatusEnum.PENDING.getCode()).count());
		vo.setPendingUserAuthentications(userRealAuthInfoService.lambdaQuery().eq(UserRealAuthInfo::getRealAuthStatus, UserRealAuthStatusEnum.APPLYING.getCode()).count());
		vo.setPendingFinancingCertifications(userFinancingCertificationService.lambdaQuery().eq(UserFinancingCertification::getFundingStatus, UserFundingStatusEnum.APPLYING.getCode()).count());
		ArrayList<Integer> in = new ArrayList<>();
		in.add(0);
		in.add(1);
		in.add(2);
		vo.setPendingNewShareSubscriptions(newShareSubscriptionService.lambdaQuery().in(UserNewShareSubscription::getSubscriptionStatus, in).count());
		
		Date date = new Date();
		vo.setServiceTimestamp(date.getTime());
		
		ZonedDateTime beijingTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
		Timestamp beijingTimestamp = Timestamp.valueOf(beijingTime.toLocalDateTime());
		ZonedDateTime newYorkTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
		Timestamp newYorkTimestamp = Timestamp.valueOf(newYorkTime.toLocalDateTime());
		vo.setBeijingTimestamp(beijingTimestamp.getTime());
		vo.setNewYorkTimestamp(newYorkTimestamp.getTime());
		return Response.successData(vo);
	}
}
