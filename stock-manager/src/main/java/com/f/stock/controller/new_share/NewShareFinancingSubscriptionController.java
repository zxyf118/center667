package com.f.stock.controller.new_share;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.IpAddressService;
import service.SiteInternalMessageService;
import service.UserNewShareSubscriptionService;
import vo.manager.NewShareSubscriptionListVO;
import vo.manager.NewShareSubscriptionSearchParamVO;

@RestController
@RequestMapping("/newShare/financingSubscription")
@Api(tags = "新股管理")
public class NewShareFinancingSubscriptionController extends BaseController {
	
	@Resource
	private UserNewShareSubscriptionService userNewShareSubscriptionService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private SiteInternalMessageService siteInternalMessageService;
	
	@ApiOperation("融资申购记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<NewShareSubscriptionListVO>> list(@RequestBody NewShareSubscriptionSearchParamVO param) {
		Page<NewShareSubscriptionListVO> page = new Page<NewShareSubscriptionListVO>(param.getPageNo(), param.getPageSize());
		param.setSubscriptionType(2);
		userNewShareSubscriptionService.managerList(page, param);
		return Response.successData(page);
	}
	@ApiOperation("融资申购记录-中签")
	@PostMapping("/win")
	@ResponseBody
	public Response<Void> win(@ApiParam("申购记录id") @RequestParam("id") Integer id, @ApiParam("中签数量") @RequestParam("awardQuantity") Integer awardQuantity) {
		return userNewShareSubscriptionService.managerWin(id, awardQuantity, getIp(), super.getUser().getOperator());
	}
	@ApiOperation("融资申购记录-未中签")
	@PostMapping("/fail")
	@ResponseBody
	public Response<Void> fail(@ApiParam("申购记录id") @RequestParam("id") Integer id) {
		return userNewShareSubscriptionService.managerFail(id, getIp(), super.getUser().getOperator());
	}
	
	@ApiOperation("融资申购记录-转入持仓")
	@PostMapping("/transfer")
	@ResponseBody
	public Response<Void> transfer(@ApiParam("申购记录id") @RequestParam("id") Integer id) {
		return userNewShareSubscriptionService.managerTransfer(id, getIp(), super.getUser().getOperator());
	}
}
