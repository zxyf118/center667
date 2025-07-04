package com.f.stock.controller.market;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import entity.SiteAnnouncement;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.SiteAnnouncementService;

@Controller
@RequestMapping("market/announcement")
@Api(tags = "交易和市场")
public class AnnouncementController {
	
	@Resource
	private SiteAnnouncementService announcementService;
	
	@ApiOperation("公告列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<List<SiteAnnouncement>> list() {
		List<SiteAnnouncement> list = announcementService.lambdaQuery()
					.eq(SiteAnnouncement::getIsShow, true)
					.orderByAsc(SiteAnnouncement::getSort).orderByDesc(SiteAnnouncement::getAddTime)
					.select(SiteAnnouncement::getTitle, SiteAnnouncement::getSource, SiteAnnouncement::getContent).list();
		return Response.successData(list);
	}
}
