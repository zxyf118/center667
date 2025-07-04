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

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.AgentInfoService;
import service.AgentInvitationCodeService;
import vo.manager.AddAgentParamVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.EditAgentParamVO;

@RestController
@RequestMapping("/agent")
@Api(tags = "用户管理")
public class AgentController extends BaseController {
	
	@Resource
	private AgentInfoService agentInfoService;
	
	@Resource
	private AgentInvitationCodeService agentInvitationCodeService;
	
	@ApiOperation("代理列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<AgentListVO>> list(@RequestBody AgentListSearchParamVO param) {
		Page<AgentListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		agentInfoService.managerAgentList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("代理列表-添加代理")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody AddAgentParamVO param) {
		return agentInfoService.mamangerAddAgent(param, getUser().getOperator());
	}
	
	@ApiOperation("代理列表-修改代理")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody EditAgentParamVO param) {
		return agentInfoService.mamangerEditAgent(param);
	}
	
	@ApiOperation("代理列表-添加邀请码")
	@PostMapping("/addInvitationCode")
	@ResponseBody
	public Response<Void> addInvitationCode (
			@ApiParam("代理ID") @RequestParam("agentId") Integer agentId, 
			@ApiParam("邀请码") @RequestParam("invitationCode") String invitationCode,
			@ApiParam("备注") @RequestParam("remark") String remark
			) {
		return agentInvitationCodeService.managerAddInvitationCode(agentId, invitationCode, remark, getUser().getOperator());
	}
}
