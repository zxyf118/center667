package com.f.stock.controller.assets;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.f.stock.controller.BaseController;

import annotation.RequestLimit;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserFinancingCertificationService;
import service.UserInfoService;
import service.UserRealAuthInfoService;
import vo.common.TokenUserVO;
import vo.server.AddUserRealAuthInfoVO;
import vo.server.AssetsDetailVO;

@Controller
@RequestMapping("/assets")
@Api(tags = "资产")
public class AssetsController extends BaseController {
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserRealAuthInfoService userRealAuthInfoService;
	
	@Resource
	private UserFinancingCertificationService userFinancingCertificationService;
	
	@ApiOperation("资产明细")
	@PostMapping("/detail")
	@ResponseBody
	public Response<AssetsDetailVO> detail() {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.assetsDetail(tu.getLoginId());
	}
	
	@ApiOperation("提交实名认证申请")
	@PostMapping("/submitRealAuthInfo")
	@ResponseBody
	@RequestLimit
	public Response<Void> submitRealAuthInfo(@RequestBody AddUserRealAuthInfoVO param) {
		TokenUserVO tu = super.getTokenUser();
		return userRealAuthInfoService.submitRealAuthInfo(tu.getLoginId(), param, super.getIp());
	}
	
	@ApiOperation("提交融资开户申请")
	@PostMapping("/submitFinancingCertification")
	@ResponseBody
	@RequestLimit
	public Response<Void> submitFinancingCertification() {
		TokenUserVO tu = super.getTokenUser();
		return userFinancingCertificationService.submitFinancingCertification(tu.getLoginId(), super.getIp());
	}
}
