package com.f.stock.schema;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import entity.DailyStockQuotes;
import entity.StockDataChangeRecord;
import entity.StockInfo;
import enums.StockDataChangeTypeEnum;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import service.DailyStockQuotesService;
import service.StockInfoService;
import utils.EastMoneyApiAnalysis;
import utils.SinaApi;
import vo.common.StockQuotesVO;

@Component
@Slf4j
public class DailyStockQuotesTask {

	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private DailyStockQuotesService dailyStockQuotesService;
	
	

	/* 每天16點股票數據定時存入數據庫（股票走勢圖數據存儲） */
	@Scheduled(cron = "0 0 16 ? * MON-FRI")
	@Async("stockTask")
	public void doTask() {
		log.info("【保存股票日内行情 定时任务】 开始保存 ... ");
		List<StockInfo> list = stockInfoService.list();
		 Date nowDate = new Date();
		 SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		 SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
         String ymd_date = sdf1.format(nowDate);
         dailyStockQuotesService.lambdaUpdate().eq(DailyStockQuotes::getYmd, ymd_date).remove();
         String hm_date = sdf2.format(nowDate);
         try {
			list.forEach(i->{
				StockQuotesVO sqv;
				if(i.getStockType().equals(StockTypeEnum.US.getCode()) || i.getStockType().equals(StockTypeEnum.HK.getCode())) {
					sqv = EastMoneyApiAnalysis.getStockRealTimeData(i.getStockType(), i.getStockCode());
				} else {
					sqv = SinaApi.getSinaStock(i.getStockType(), i.getStockCode());
				}
				if(sqv.getGid() == null) {
					log.info("{}({})查询不到行情，系统将自动删除股票信息", i.getStockName(), i.getStockCode());
					StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
					stockDataChangeRecord.setStockId(i.getId());
					stockDataChangeRecord.setStockCode(i.getStockCode());
					stockDataChangeRecord.setStockName(i.getStockName());
					stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.DELETE_STOCK.getCode());
					stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.DELETE_STOCK.getName());
					stockDataChangeRecord.setOldContent("股票名称：" + i.toString());
					stockDataChangeRecord.setIp("127.0.0.1");
					stockDataChangeRecord.setIpAddress("服务器本机地址");
					stockDataChangeRecord.setOperator("系统");
					stockDataChangeRecord.insert();
					i.deleteById();
				} else {
					DailyStockQuotes dsq = new DailyStockQuotes();
					dsq.setStockId(i.getId());
					if(sqv.getStockName() != null && !i.getStockName().equals(sqv.getStockName())) {
						log.info("{}和行情的股票名称不一致，系统将自动更新股票名称为{}，", i.getStockName(), sqv.getStockName());
						stockInfoService.lambdaUpdate().eq(StockInfo::getId, i.getId()).set(StockInfo::getStockName, sqv.getStockName()).update();
						StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
						stockDataChangeRecord.setStockId(i.getId());
						stockDataChangeRecord.setStockCode(i.getStockCode());
						stockDataChangeRecord.setStockName(i.getStockName());
						stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.EDIT_STOCK.getCode());
						stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.EDIT_STOCK.getName());
						stockDataChangeRecord.setOldContent("股票名称：" + i.getStockName());
						stockDataChangeRecord.setNewContent("股票名称：" + sqv.getStockName());
						stockDataChangeRecord.setIp("127.0.0.1");
						stockDataChangeRecord.setIpAddress("服务器本机地址");
						stockDataChangeRecord.setOperator("系统");
						stockDataChangeRecord.insert();
					}
					dsq.setStockName(i.getStockName());
					dsq.setStockCode(i.getStockCode());
					dsq.setStockType(i.getStockType());
					dsq.setYmd(ymd_date);
					dsq.setHm(hm_date);
					dsq.setNowPrice(sqv.getNowPrice());
					dsq.setPercentageIncrease(sqv.getPercentageIncrease());
					dsq.setOpenPrice(sqv.getOpenPrice());
					dsq.setPrevClose(sqv.getPrevClose());
					dsq.setVolume(sqv.getVolume());
					dsq.setTurnover(sqv.getTurnover());
					dsq.insert();
				}
			});
         } catch(Exception ex) {
        	 log.info("【保存股票日内行情 定时任务】 异常 ... ", ex);
         }
		log.info("【保存股票日内行情 定时任务】 结束 ... ");
	}
}
