package com.f.stock.controller.assets;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserNewShareSubscriptionService;
import vo.common.TokenUserVO;
import vo.server.UserNewShareSubscriptionDetailVO;
import vo.server.UserNewShareSubscriptionListVO;

@Controller
@RequestMapping("/assets/newShare")
@Api(tags = "资产")
public class NewShareController extends BaseController {
	
	@Resource
	private UserNewShareSubscriptionService userNewShareSubscriptionService;
	
	@ApiOperation("新股申购列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserNewShareSubscriptionListVO>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize,
			@ApiParam(value = "申购类型：0:0元申购 ,1:现金申购 ,2:融资申购")  @RequestParam(value = "subscriptionType", defaultValue = "", required = false) Integer subscriptionType
			
			) {
		Page<UserNewShareSubscriptionListVO> page = new Page<>(pageNo, pageSize);
		this.userNewShareSubscriptionService.userNewShareSubscriptionList(page, super.getTokenUser().getLoginId(), subscriptionType);
		return Response.successData(page);
	}
	
	@ApiOperation("新股申购详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<UserNewShareSubscriptionDetailVO> detail(@ApiParam("申购订单id") @RequestParam(value = "id") Integer id) {
		return userNewShareSubscriptionService.userNewShareSubscriptionDetail(id, super.getTokenUser().getLoginId());
	}
	
	@ApiOperation("新股申购确认缴纳")
	@PostMapping("/pay")
	@ResponseBody
	public Response<Void> pay(@ApiParam("申购记录id") @RequestParam("id") Integer id) {
		TokenUserVO tu = super.getTokenUser();
		return userNewShareSubscriptionService.userPay(id, tu.getLoginId(), getIp(), tu.getOperator());
	}
	
	@ApiOperation("新股取消、撤单")
	@PostMapping("/cancel")
	@ResponseBody
	public Response<Void> cannel(@ApiParam("申购记录id") @RequestParam(value = "id") Integer id) {
		TokenUserVO tu = super.getTokenUser();
		return userNewShareSubscriptionService.userCancel(id, tu.getLoginId(), super.getIp(), tu.getOperator());
	}
}
