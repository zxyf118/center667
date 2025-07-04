package com.f.stock.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFinancingCertification;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserFinancingCertificationService;
import vo.manager.UserFinancingCertificationListSearchVO;

@RestController
@RequestMapping("/user/financingCertification")
@Api(tags = "用户管理")
public class UserFinancingCertificationController extends BaseController {
	
	@Resource
	private UserFinancingCertificationService userFinancingCertificationService;
	
	@ApiOperation("融资审核")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserFinancingCertification>> list(@RequestBody UserFinancingCertificationListSearchVO param) {
		Page<UserFinancingCertification> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFinancingCertificationService.managerUserFinancingCertificationList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("融资审核-融资状态修改")
	@PostMapping("/updateFundingStatus")
	@ResponseBody
	public Response<Void> updateFundingStatus(
			@ApiParam("订单ID") @RequestParam("id") Integer id, 
			@ApiParam("操作状态，2-通过,3-未审核通过") @RequestParam(value = "fundingStatus") Integer fundingStatus) {
		return userFinancingCertificationService.managerUpdateFundingStatus(id, fundingStatus, getIp(), getUser().getOperator());
	}
}
