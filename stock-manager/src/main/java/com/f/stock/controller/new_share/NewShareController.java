package com.f.stock.controller.new_share;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.NewShare;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.NewShareService;
import utils.StringUtil;
import vo.manager.NewShareListSearchParamVO;
import vo.manager.NewShareVO;

@RestController
@RequestMapping("/newShare")
@Api(tags = "新股管理")
public class NewShareController extends BaseController {

	@Resource
	private NewShareService newShareService;

	@ApiOperation("新股列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<NewShare>> list(@RequestBody NewShareListSearchParamVO param) {
		Page<NewShare> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<NewShare> lqw = new LambdaQueryWrapper<>();
		if(param.getId() != null && param.getId() > 0) {
			lqw.eq(NewShare::getId, param.getId());
		}
		if (!StringUtil.isEmpty(param.getStockCodeOrName())) {
			lqw.or(l -> l.like(NewShare::getStockCode, param.getStockCodeOrName()).or().like(NewShare::getStockName,
					param.getStockCodeOrName()));
		}
		if (!StringUtil.isEmpty(param.getStockType())) {
			lqw.eq(NewShare::getStockType, param.getStockType());
		}
		if (!StringUtil.isEmpty(param.getStockPlate())) {
			lqw.eq(NewShare::getStockPlate, param.getStockPlate());
		}
		if (param.getIsShow() != null) {
			lqw.eq(NewShare::getIsShow, param.getIsShow());
		}
		if (param.getIsLock() != null) {
			lqw.eq(NewShare::getIsLock, param.getIsLock());
		}
		if (param.getAddTimeStart() != null) {
			lqw.ge(NewShare::getAddTime, param.getAddTimeStart());
		}
		if (param.getAddTimeEnd() != null) {
			lqw.le(NewShare::getAddTime, param.getAddTimeEnd());
		}
		if (param.getSubscriptionType() != null && param.getSubscriptionType() > 0) {
			if (param.getSubscriptionType() == 1) {
				lqw.le(NewShare::getEnableCashSubscription, true);
			} else {
				lqw.le(NewShare::getEnableFinancingSubscription, true);
			}
		}
		lqw.orderByDesc(NewShare::getId);
		newShareService.page(page, lqw);
		return Response.successData(page);
	}

	@ApiOperation("新股列表--添加新股")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody NewShareVO param) {
		return newShareService.add(param, getIp(), getUser().getOperator());
	}

	@ApiOperation("新股列表--修改新股")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody NewShareVO param) {
		return newShareService.edit(param, getIp(), getUser().getOperator());
	}

	@ApiOperation("新股列表--删除新股")
	@PostMapping("/delele")
	@ResponseBody
	public Response<Void> delele(@ApiParam("新股id") @RequestParam("id") Integer id) {
		return newShareService.delete(id, getIp(), getUser().getOperator());
	}
}
