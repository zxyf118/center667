package com.f.stock.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import entity.BankInfo;
import entity.Country;
import entity.common.Response;
import entity.common.StockParamConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.BankInfoService;
import service.CountryService;
import service.SysParamConfigService;

@Controller
@RequestMapping("/list")
@Api(tags = "列表数据")
public class ListController {
	
	@Resource
	private CountryService countryService;
	
	@Resource
	private BankInfoService bankInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@ApiOperation("国家地区")
	@PostMapping("/country")
	@ResponseBody
	public Response<List<Country>> country() {
		List<Country> list = countryService.countryList();
		return Response.successData(list);
	}
	
	@ApiOperation("银行列表")
	@PostMapping("/bank")
	@ResponseBody
	public Response<List<BankInfo>> bank() {
		List<BankInfo> list = bankInfoService.lambdaQuery().eq(BankInfo::getIsShow, true).list();
		return Response.successData(list);
	}
	
	@ApiOperation("股票系统配置")
	@PostMapping("/stockParamConfig")
	@ResponseBody
	public Response<StockParamConfig> stockParamConfig() {
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		return Response.successData(spc);
	}
	
}
