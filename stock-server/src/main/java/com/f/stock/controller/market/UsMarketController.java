package com.f.stock.controller.market;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import annotation.NotNeedAuth;
import config.RedisDbTypeEnum;
import entity.common.Response;
import enums.StockMarketTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import redis.RedisKeyPrefix;
import service.NewShareService;
import service.StockInfoService;
import utils.RedisDao;
import vo.manager.ServerNewShareSearchParamVO;
import vo.server.ServerNewShareVO;
import vo.server.StockData;
import vo.server.StockIndexQuotesVO;
import vo.server.StockPlateQuotesVO;

@Controller
@RequestMapping("/market/us")
@Api(tags = "交易和市场")
public class UsMarketController {
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private NewShareService newShareService;
	
	@Resource
	private RedisDao redisDao;
	
	private final String stockTypeIn = "'us'";
	
	@ApiOperation("美股列表")
	@PostMapping("/stockList")
	@NotNeedAuth
	@ResponseBody
	public Response<Page<StockData>> stockList (
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize,
			@ApiParam("排序条件(最新价：nowPrice,涨幅比例：percentageIncrease),成交量：turnover") @RequestParam(value = "sortTerm", defaultValue = "percentageIncrease", required = false) String sortTerm,
			@ApiParam("排序类型(升序：asc，降序：desc)") @RequestParam(value = "sortType", defaultValue = "desc", required = false) String sortType
			) {
		Page<StockData> page = stockInfoService.getStockQuoteListPage(pageNo, pageSize, sortTerm, sortType,RedisKeyPrefix.getUsStockQuoteListKey());
		return Response.successData(page);
	}
	
	@ApiOperation("美股指数行情")
	@PostMapping("/stockIndexQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockIndexQuotesVO>> stockIndexQuotes() {
		List<StockIndexQuotesVO> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getUsStockIndexQuotesKey(), StockIndexQuotesVO.class);
		return Response.successData(list);
	}
	
	@ApiOperation("美股新股列表")
	@PostMapping("/newShares")
	@NotNeedAuth
	@ResponseBody
	public Response<Page<ServerNewShareVO>> newShares(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize
			) {
		Page<ServerNewShareVO> page = new Page<>(pageNo, pageSize);
		ServerNewShareSearchParamVO param = new ServerNewShareSearchParamVO();
		param.setStockTypeIn(stockTypeIn);
		this.newShareService.getNewSharePageByStockType(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("美股板块")
	@PostMapping("/stockPlateQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockPlateQuotesVO>> stockPlateQuotes() {
		List<StockPlateQuotesVO> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getUsStockPlateQuotesKey(), StockPlateQuotesVO.class);
 		return Response.successData(list);
	}
	
	@ApiOperation("美股近一个月涨幅榜")
	@PostMapping("/increaseRateRank")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockData>> increaseRateRank() {
		return Response.successData(stockInfoService.getIncreaseRateRank(1,20,StockMarketTypeEnum.Market_Us.getCode()));
	}
}
