package com.f.stock.schema;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import entity.NewShare;
import entity.NewShareDataChangeRecord;
import enums.NewShareDataChangeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import service.NewShareService;
import utils.StringUtil;

@Component
@Slf4j
public class EastMoneyNewShareTask {
	
	private final String getNewShareUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?sortColumns=APPLY_DATE,SECURITY_CODE&sortTypes=-1,-1&reportName=RPTA_APP_IPOAPPLY&columns=SECURITY_CODE,SECURITY_NAME,TRADE_MARKET_CODE,APPLY_CODE,TRADE_MARKET,MARKET_TYPE,ORG_TYPE,ISSUE_NUM,ONLINE_ISSUE_NUM,OFFLINE_PLACING_NUM,TOP_APPLY_MARKETCAP,PREDICT_ONFUND_UPPER,ONLINE_APPLY_UPPER,PREDICT_ONAPPLY_UPPER,ISSUE_PRICE,LATELY_PRICE,CLOSE_PRICE,APPLY_DATE,BALLOT_NUM_DATE,BALLOT_PAY_DATE,LISTING_DATE,AFTER_ISSUE_PE&quoteColumns=f2~01~SECURITY_CODE~NEWEST_PRICE&quoteType=0&filter=(APPLY_DATE%3E%272022-06-20%27)&source=WEB&client=WEB";
	
	@Resource
	private NewShareService newShareService;
	
	 /**
     * 每天工作日 下午4点执行一次  
     */
    @Scheduled(cron = "0 0 16 * * MON-FRI")
    @Async("stockTask")
	public void getNewShare() {
    	 log.info("----------每天工作日下午4点抓取新股日历数据开始--------------");
    	 try {
    		 int addCount = 0;
    		 int pageNumber = 1;
    		 int pageSize = 35;
    		 int count = 0;
    		 int pages = 0;
    		 Date now = new Date();
    		 do {
	    		 String url = getNewShareUrl + "&pageSize=" + pageSize + "&pageNumber=" + pageNumber;
	    		 String ret = HttpRequest.get(url).execute().body();
	    		 if(StringUtil.isEmpty(ret)) {
	    			 log.info("----------每天工作日下午4点抓取新股日历数据返回空, 第{}页", pageNumber);
	    			 break;
	    		 }
	    		 JSONObject json = new JSONObject(ret);
	    		 if(!json.getBool("success")) {
	    			 log.info("----------每天工作日下午4点抓取新股日历数据返回失败状态，第{}页", pageNumber);
	    			 break;
	    		 }
	    		 JSONObject result = json.getJSONObject("result");
	    		 pages = result.getInt("pages");
	    		 JSONArray data = result.getJSONArray("data");
	    		 for (int i = 0; i < data.size(); i++) {
	    			 JSONObject o = data.getJSONObject(i);
	    			 String stockCode = o.getStr("SECURITY_CODE");
	    			 String stockName = o.getStr("SECURITY_NAME");
	    			 if(newShareService.lambdaQuery().eq(NewShare::getStockCode, stockCode).count() > 0) {
	    				 log.info("新股：{}({})已存在", stockName, stockCode);
	    				 continue;
	    			 }
	    			 NewShare newShare = new NewShare();
	    			 newShare.setStockCode(stockCode);
	    			 newShare.setStockName(stockName);
	    			 String TRADE_MARKET = o.getStr("TRADE_MARKET");
	    			 switch(TRADE_MARKET) {
	    			 case "深圳证券交易所":
	    				 newShare.setStockType("sz");
	    				 break;
	    			 case "上海证券交易所":
	    				 newShare.setStockType("sh");
	    			 default :
	    				 newShare.setStockType("bj");
	    				 break;
	    			 }
	    			 String MARKET_TYPE = o.getStr("MARKET_TYPE");
	    			 if(MARKET_TYPE.equals("科创板")) {
	    				 newShare.setStockPlate("科创");
	    			 } else  if(MARKET_TYPE.equals("创业板")) {
	    				 newShare.setStockPlate("创业");
	    			 }
	    			 newShare.setPrice(o.getBigDecimal("ISSUE_PRICE", BigDecimal.ZERO));
	    			 newShare.setMaxBuyingShares(o.getInt("ONLINE_ISSUE_NUM", 0));
	    			 newShare.setIssueShares(o.getInt("ISSUE_NUM", 0) * 10000);
	    			 newShare.setSubscriptionDeadline(o.getDate("APPLY_DATE"));
	    			 newShare.setPaymentDeadline(o.getDate("BALLOT_PAY_DATE"));
	    			 newShare.setListingDate(o.getDate("LISTING_DATE"));
	    			 newShare.setIsLock(false);
	    			 newShare.setIsShow(true);
	    			 newShare.setEnableZeroSubscription(true);
	    			 newShare.setEnableCashSubscription(true);
	    			 newShare.setEnableFinancingSubscription(true);
	    			 newShare.setAddTime(now);
	    			 newShare.setCreator("系统");
	    			 newShare.insert();
	    			 NewShareDataChangeRecord nsdcr = new NewShareDataChangeRecord();
	    			 nsdcr.setNewShareId(newShare.getId());
	    			 nsdcr.setStockCode(stockCode);
	    			 nsdcr.setStockName(stockName);
	    			 nsdcr.setDataChangeTypeCode(NewShareDataChangeTypeEnum.ADD_NEW_SHARE.getCode());
	    			 nsdcr.setDataChangeTypeName(NewShareDataChangeTypeEnum.ADD_NEW_SHARE.getName());
	    			 nsdcr.setNewContent(newShare.toString());
	    			 nsdcr.setIp("127.0.0.1");
	    			 nsdcr.setIpAddress("服务器本机ip");
	    			 nsdcr.setOperator("系统");
	    			 nsdcr.setCreateTime(now);
	    			 nsdcr.insert();
	    			 addCount++;
	    		 }
    		 } while(pageNumber++ < pages);
    		 log.info("----------每天工作日下午4点抓取新股日历数据结束，总获取到{}条新股信息,新增新股数量：{}--------------", count, addCount);
    		 
    	 } catch(Exception ex) {
    		 log.error("抓取新股日历数据", ex);
    	 }
	}
}
