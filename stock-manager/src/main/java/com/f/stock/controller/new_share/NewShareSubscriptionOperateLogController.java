package com.f.stock.controller.new_share;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserNewShareSubscriptionOperateLogService;
import vo.manager.NewShareSubscriptionOperateLogSearchParamVO;
import vo.manager.NewShareSubscriptionOperateLogVO;

@RestController
@RequestMapping("/newShare/subscription/operate")
@Api(tags = "新股管理")
public class NewShareSubscriptionOperateLogController {
	@Resource
	private UserNewShareSubscriptionOperateLogService userNewShareSubscriptionOperateLogService;
	
	@ApiOperation("新股申购操作记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<NewShareSubscriptionOperateLogVO>> list(@RequestBody NewShareSubscriptionOperateLogSearchParamVO param) {
		Page<NewShareSubscriptionOperateLogVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userNewShareSubscriptionOperateLogService.managerList(page, param);
		return Response.successData(page);
	}
}
