package com.f.stock.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import entity.AgentInfo;
import entity.BankInfo;
import entity.Country;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CashinTypeEnum;
import enums.CashoutTypeEnum;
import enums.NewShareDataChangeTypeEnum;
import enums.StockDataChangeTypeEnum;
import enums.StockRealTimeApiEnum;
import enums.StockTypeEnum;
import enums.UserDataChangeTypeEnum;
import enums.UserNewShareSubscriptionStatusEnum;
import enums.UserStockPositionChangeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.AgentInfoService;
import service.BankInfoService;
import service.CountryService;
import vo.common.TypeVO;

@RestController
@RequestMapping("/list")
@Api(tags = "查询条件的下拉和其它列表数据")
public class ListController {
	
	@Resource
	private CountryService countryService;
	
	@Resource
	private AgentInfoService agentInfoService;
	
	@Resource
	private BankInfoService bankInfoService;
	
	@ApiOperation("国家地区")
	@PostMapping("/country")
	@ResponseBody
	public Response<List<Country>> country() {
		List<Country> list = countryService.countryList();
		return Response.successData(list);
	}
	
	@ApiOperation("代理列表")
	@PostMapping("/agent")
	@ResponseBody
	public Response<List<AgentInfo>> agent() {
		List<AgentInfo> list = agentInfoService.list();
		return Response.successData(list);
	}
	
	@ApiOperation("银行列表")
	@PostMapping("/bank")
	@ResponseBody
	public Response<List<BankInfo>> bank() {
		List<BankInfo> list = bankInfoService.list();
		return Response.successData(list);
	}
	
	@ApiOperation("用户资料变更类型列表")
	@PostMapping("/userDataChageType")
	public Response<List<TypeVO>> userDataChageType() {
		List<TypeVO> list = new ArrayList<>();
		for(UserDataChangeTypeEnum e : UserDataChangeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("股票信息变更类型列表")
	@PostMapping("/stockDataChageType")
	public Response<List<TypeVO>> stockDataChageType() {
		List<TypeVO> list = new ArrayList<>();
		for(StockDataChangeTypeEnum e : StockDataChangeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("股票持仓信息变化类型列表")
	@PostMapping("/userStockPositionChangeType")
	public Response<List<TypeVO>> userStockPositionChangeType() {
		List<TypeVO> list = new ArrayList<>();
		for(UserStockPositionChangeTypeEnum e : UserStockPositionChangeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("股票类型列表")
	@PostMapping("/stockType")
	public Response<List<TypeVO>> stockType() {
		List<TypeVO> list = new ArrayList<>();
		for(StockTypeEnum e : StockTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("资金变化类型列表")
	@PostMapping("/amtDeType")
	public Response<List<TypeVO>> amtDeType() {
		List<TypeVO> list = new ArrayList<>();
		for(AmtDeTypeEnum e : AmtDeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	@ApiOperation("新股资料变化类型列表")
	@PostMapping("/newShareDataChangeType")
	public Response<List<TypeVO>> newShareDataChangeType() {
		List<TypeVO> list = new ArrayList<>();
		for(NewShareDataChangeTypeEnum e : NewShareDataChangeTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	@ApiOperation("充值方式类型列表")
	@PostMapping("/cashinType")
	public Response<List<TypeVO>> cashinType() {
		List<TypeVO> list = new ArrayList<>();
		for(CashinTypeEnum e : CashinTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	@ApiOperation("提现方式类型列表")
	@PostMapping("/cashoutType")
	public Response<List<TypeVO>> cashoutType() {
		List<TypeVO> list = new ArrayList<>();
		for(CashoutTypeEnum e : CashoutTypeEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("股票实时行情API列表")
	@PostMapping("/stockRealTimeApiType")
	public Response<List<TypeVO>> stockRealTimeApiType() {
		List<TypeVO> list = new ArrayList<>();
		for(StockRealTimeApiEnum e : StockRealTimeApiEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(e.getCode());
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
	
	@ApiOperation("新股申购操作类型列表")
	@PostMapping("/userNewShareSubscriptionStatusType")
	public Response<List<TypeVO>> userNewShareSubscriptionStatusType() {
		List<TypeVO> list = new ArrayList<>();
		for(UserNewShareSubscriptionStatusEnum e : UserNewShareSubscriptionStatusEnum.values()) {
			TypeVO json = new TypeVO();
			json.setCode(String.valueOf(e.getCode()));
			json.setName(e.getName());
			list.add(json);
		}
		return Response.successData(list);
	}
}
