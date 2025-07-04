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
@RequestMapping("/market/a")
@Api(tags = "交易和市场")
public class AMarketController {
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private NewShareService newShareService;
	
	private final String stockTypeIn = "'sz','sh','bj'";
	
	@ApiOperation("A股列表")
	@PostMapping("/stockList")
	@NotNeedAuth
	@ResponseBody
	public Response<Page<StockData>> aStockList(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize,
			@ApiParam("排序条件(最新价：nowPrice,涨幅比例：percentageIncrease),成交量：turnover") @RequestParam(value = "sortTerm", defaultValue = "percentageIncrease", required = false) String sortTerm,
			@ApiParam("排序类型(升序：asc，降序：desc)") @RequestParam(value = "sortType", defaultValue = "desc", required = false) String sortType
			) {
		Page<StockData> page = stockInfoService.getStockQuoteListPage(pageNo, pageSize, sortTerm, sortType,RedisKeyPrefix.getAStockQuoteListKey());
		return Response.successData(page);
	}
	
	@ApiOperation("A股指数行情")
	@PostMapping("/stockIndexQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockIndexQuotesVO>> stockIndexQuotes() {
		List<StockIndexQuotesVO> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getAStockIndexQuotesKey(), StockIndexQuotesVO.class);
		return Response.successData(list);
	}
	
	@ApiOperation("A股新股列表")
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
	
	@ApiOperation("A股行业板块")
	@PostMapping("/stockIndustryPlateQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockPlateQuotesVO>> stockIndustryPlateQuotes() {
		List<StockPlateQuotesVO> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getAStockIndustryPlateQuotesKey(), StockPlateQuotesVO.class);
 		return Response.successData(list);
	}
	
	@ApiOperation("A股概念板块")
	@PostMapping("/stockConceptPlateQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockPlateQuotesVO>> stockConceptPlateQuotes() {
		List<StockPlateQuotesVO> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getAStockConceptPlateQuotesKey(), StockPlateQuotesVO.class);
 		return Response.successData(list);
	}
	
	@ApiOperation("A股近一个月涨幅榜")
	@PostMapping("/increaseRateRank")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockData>> increaseRateRank() {
		return Response.successData(stockInfoService.getIncreaseRateRank(1,20,StockMarketTypeEnum.Market_A.getCode()));
	}
}
