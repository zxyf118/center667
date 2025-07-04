package com.f.stock.schema;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import config.RedisDbTypeEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import redis.RedisKeyPrefix;
import service.SysParamConfigService;
import utils.EastMoneyApiAnalysis;
import utils.HttpClientRequest;
import utils.RedisDao;
import utils.StringUtil;
import vo.server.StockData;

@Component
@Slf4j
public class EastMoneyStockMarketTask {
//	private String hkStockUrl = "http://32.push2delay.eastmoney.com/api/qt/clist/get?pn=1&pz=9999&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&wbp2u=%7C0%7C0%7C0%7Cweb&fid=f3&fs=m:128+t:3,m:128+t:4,m:128+t:1,m:128+t:2&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f19,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f168&_=";
//	private String usStockUrl = "http://push2.eastmoney.com/api/qt/clist/get?np=1&fltt=1&invt=2&fs=m%3A105%2Cm%3A106%2Cm%3A107&fields=f12%2Cf13%2Cf14%2Cf1%2Cf2%2Cf4%2Cf3%2Cf152%2Cf17%2Cf28%2Cf15%2Cf16%2Cf18%2Cf20%2Cf115&fid=f3&pn=1&pz=200000&po=1&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=2167077428955438%7C0%7C1%7C0%7Cweb&_=1742898043096";

	@Resource
	private RedisDao redisDao;
	@Resource
	private SysParamConfigService sysParamConfigService;
	
/**===================已废弃，无需使用=====================**/
//	/**
//	 * 港股信息-每隔15s执行一次
//	 */
//	@Scheduled(fixedDelay = 15000)
//	@Async("stockTask")
//	public void getHkStockInfo() {
//		log.info("====================从东财获取港股信息任务开始====================");
//		try {
//			//发起请求
//	        String ret = HttpClientRequest.doGet(hkStockUrl + System.currentTimeMillis());
//	       // log.info("东财返回港股信息:{}", ret);
//	        if(StringUtil.isEmpty(ret)) {
//	        	return;
//	        }
//	        JSONObject json = new JSONObject(ret);
//	        JSONArray js = json.getJSONObject("data").getJSONArray("diff");
//	        for (Object o : js) {
//	        	 JSONObject data = (JSONObject) o;
//	             String stockCode = data.getStr("f12");
//	             //存入redis
//	             redisDao.setString(RedisDbTypeEnum.STOCK_HK, "hk" + stockCode, data.toString());
//	        }
//	        log.info("港股信息存入redis成功");
//		} catch(Exception ex) {
//			log.error("从东财获取港股信息任务异常", ex);
//		} finally {
//			log.info("====================从东财获取港股信息任务结束====================");
//		}
//	}
//	
//	/**
//	 * 美股信息-每隔15s执行一次
//	 */
//	@Scheduled(fixedDelay = 15000)
//	@Async("stockTask")
//	public void getUsStockInfo() {
//		log.info("====================从东财获取美股信息任务开始====================");
//		try {
//			//发起请求
//			String ret = HttpClientRequest.doGet(usStockUrl + System.currentTimeMillis());
//	     //   log.info("东财返回美股信息:{}", ret);
//	        if(StringUtil.isEmpty(ret)) {
//	        	return;
//	        }
//	        JSONObject json = new JSONObject(ret);
//	        JSONArray js = json.getJSONObject("data").getJSONArray("diff");
//	        for (Object o : js) {
//	        	 JSONObject data = (JSONObject) o;
//	             String stockCode = data.getStr("f12");
//	             //存入redis
//	             redisDao.setString(RedisDbTypeEnum.STOCK_US, "us" + stockCode, data.toString());
//	        }
//	        log.info("美股信息存入redis成功");
//		} catch (Exception ex) {
//			log.error("从东财获取美股信息任务异常", ex);
//		} finally {
//			log.info("====================从东财获取美股信息任务结束====================");
//		}
//	}
/**===================已废弃，无需使用=====================**/
	
	/**
	 * 获取A股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getAStockQuoteList() {
		//执行A股涨幅排行榜数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_A.getCode(), RedisKeyPrefix.getAStockQuoteListKey());
	}
	
	/**
	 * 获取美股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getUsStockQuoteList() {
		//执行-美股涨幅排行数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_Us.getCode(), RedisKeyPrefix.getUsStockQuoteListKey());
	}
	
	/**
	 * 获取港股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getHkStockQuoteList() {
		//执行-港股涨幅排行数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_Hk.getCode(), RedisKeyPrefix.getHkStockQuoteListKey());

	}
	
	/**
	 * 执行-股票涨幅排行榜请求
	 * @param stockMarketTypeCode 股票市场类型
	 * @param redisKey 缓存key
	 * @throws Exception 
	 */
	private void doGetStockQuoteList(int stockMarketTypeCode,String redisKey) {
		log.info("====================从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务开始====================");
		try {
			//1、判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(stockMarketTypeCode, RedisDbTypeEnum.STOCK_API, redisKey)) {
				log.info(StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"已收盘，暂不处理");
				return;
			}
			//2、发起请求
			String stockType = null;
			StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
			switch(stockMarketTypeEnum) {
			case Market_Hk://港股,股票类型hk
				stockType = StockTypeEnum.HK.getCode();
				break;
			case Market_Us://美股，股票类型us
				stockType = StockTypeEnum.US.getCode();
				break;
			default ://默认A股设为null
				stockType = null;
				break;
			}
			List<StockData> stockQuoteList = (List<StockData>) EastMoneyApiAnalysis.getStockList(stockType,StockData.class);
			//3、存入缓存
			if(stockQuoteList.size() > 0) {
				redisDao.setBean(RedisDbTypeEnum.STOCK_API, redisKey, stockQuoteList);
			}
		} catch(Exception ex) {
			log.error("从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务异常", ex);
		} finally {
			log.info("====================从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务结束====================");
		}
		
	}
}
