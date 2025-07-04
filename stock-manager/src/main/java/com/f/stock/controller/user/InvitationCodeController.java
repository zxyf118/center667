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

import entity.AgentInvitationCode;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.AgentInvitationCodeService;
import vo.manager.InvitationCodeListSearchParamVO;

@RestController
@RequestMapping("/invitationCode")
@Api(tags = "用户管理")
public class InvitationCodeController extends BaseController {
	
	@Resource
	private AgentInvitationCodeService agentInvitationCodeService;
	
	@ApiOperation("邀请码列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<AgentInvitationCode>> list(@RequestBody InvitationCodeListSearchParamVO param) {
		Page<AgentInvitationCode> page = new Page<>(param.getPageNo(), param.getPageSize());
		agentInvitationCodeService.managerInvitationCodeList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("邀请码列表-删除邀请码")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("邀请码ID") @RequestParam("id")Integer id) {
		agentInvitationCodeService.removeById(id);
		return Response.success();
	}
}
