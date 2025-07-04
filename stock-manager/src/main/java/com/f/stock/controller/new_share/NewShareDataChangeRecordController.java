package com.f.stock.controller.new_share;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.NewShareDataChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.NewShareDataChangeRecordService;
import utils.StringUtil;
import vo.manager.NewShareChangeParamVO;

@RestController
@RequestMapping("/newShare/dataChangeRecord")
@Api(tags = "新股管理")
public class NewShareDataChangeRecordController {
	
	@Resource
	private NewShareDataChangeRecordService newShareDataChangeRecordService;
	
	@ApiOperation("新股变更记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<NewShareDataChangeRecord>> list(@RequestBody NewShareChangeParamVO param) {
		Page<NewShareDataChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<NewShareDataChangeRecord> lqw = new LambdaQueryWrapper<>();
		if(param.getNewShareId() != null && param.getNewShareId() > 0) {
			lqw.eq(NewShareDataChangeRecord::getId, param.getNewShareId());
		}
		if (!StringUtil.isEmpty(param.getStockCodeOrName())) {
			lqw.or(l -> l.like(NewShareDataChangeRecord::getStockCode, param.getStockCodeOrName()).or().like(NewShareDataChangeRecord::getStockName,
					param.getStockCodeOrName()));
		}
		if(!StringUtil.isEmpty(param.getDataChangeTypeCode())) {
			lqw.eq(NewShareDataChangeRecord::getDataChangeTypeCode, param.getDataChangeTypeCode());
		}
		if (param.getStartTime() != null) {
			lqw.ge(NewShareDataChangeRecord::getCreateTime, param.getStartTime());
		}
		if (param.getEndTime() != null) {
			lqw.le(NewShareDataChangeRecord::getCreateTime, param.getEndTime());
		}
		lqw.orderByDesc(NewShareDataChangeRecord::getId);
		newShareDataChangeRecordService.page(page, lqw);
		return Response.successData(page);
	}
}
