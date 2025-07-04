package com.f.stock.controller.product;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.StockInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.StockInfoService;
import vo.manager.AddStockParamVO;
import vo.manager.EditStockParamVO;
import vo.manager.StockListSearchParamVO;

@RestController
@RequestMapping("/product/stock")
@Api(tags = "产品管理")
public class StockController extends BaseController {
	
	@Resource
	private StockInfoService stockInfoService;
	
	@ApiOperation("股票列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<StockInfo>> list(@RequestBody StockListSearchParamVO param) {
		Page<StockInfo> page = new Page<>(param.getPageNo(), param.getPageSize());
		stockInfoService.managerStockList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("股票列表-添加股票")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody AddStockParamVO param) {
		return stockInfoService.managerAddStock(param, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票列表-修改股票")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody EditStockParamVO param) {
		return stockInfoService.managerEditStock(param, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票列表-删除股票")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("股票ID") @RequestParam("id")Integer id) {
		return stockInfoService.managerDeleteStock(id, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票数据初始化")
	@PostMapping("/stockDataInitialize")
	@ResponseBody
	public Response<String> stockDataInitialize() {
		return stockInfoService.stockDataInitialize();
	}
}
