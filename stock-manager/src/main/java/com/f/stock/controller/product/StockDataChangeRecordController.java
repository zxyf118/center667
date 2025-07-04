package com.f.stock.controller.product;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.StockDataChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.StockDataChangeRecordService;
import utils.StringUtil;
import vo.manager.StockDataChangeRecordSearchParamVO;

@RestController
@RequestMapping("/product/stockData")
@Api(tags = "产品管理")
public class StockDataChangeRecordController {
	
	@Resource
	private StockDataChangeRecordService stockDataChangeRecordService;
	
	@ApiOperation("股票资料变更记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<StockDataChangeRecord>> list(@RequestBody StockDataChangeRecordSearchParamVO param) {
		Page<StockDataChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<StockDataChangeRecord> lqw = new LambdaQueryWrapper<>();
		if(param.getStockId() != null && param.getStockId() > 0) {
			lqw.eq(StockDataChangeRecord::getStockId, param.getStockId());
		}
		if(!StringUtil.isEmpty(param.getStockCodeOrName())) {
			lqw.or(l->l.like(StockDataChangeRecord::getStockCode, param.getStockCodeOrName()).or().like(StockDataChangeRecord::getStockName, param.getStockCodeOrName()));
		}
		if(!StringUtil.isEmpty(param.getStockDataChangeTypeCode())) {
			lqw.eq(StockDataChangeRecord::getDataChangeTypeCode, param.getStockDataChangeTypeCode());
		}
		if(param.getCreateTimeStart() != null) {
			lqw.ge(StockDataChangeRecord::getCreateTime, param.getCreateTimeStart());
		}
		if(param.getCreateTimeEnd() != null) {
			lqw.le(StockDataChangeRecord::getCreateTime, param.getCreateTimeEnd());
		}
		lqw.orderByDesc(StockDataChangeRecord::getCreateTime);
		stockDataChangeRecordService.page(page, lqw);
		return Response.successData(page);
	}
}
