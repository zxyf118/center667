package service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.AgentInfo;
import entity.AgentParentInfo;
import entity.common.Response;
import mapper.AgentInfoMapper;
import mapper.AgentParentInfoMapper;
import service.AgentInfoService;
import service.UserInfoService;
import utils.StringUtil;
import vo.common.ChildAndParentVO;
import vo.manager.AddAgentParamVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.EditAgentParamVO;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Service
public class AgentInfoServiceImpl extends ServiceImpl<AgentInfoMapper, AgentInfo> implements AgentInfoService {

	@Resource
	private AgentInfoMapper agentInfoMapper;
	
	@Resource
	private AgentParentInfoMapper agentParentInfoMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Override
	public void managerAgentList(Page<AgentListVO> page, AgentListSearchParamVO param) {
		agentInfoMapper.managerAgentList(page, param);
	}

	@Override
	@Transactional
	public Response<Void> mamangerAddAgent(AddAgentParamVO param, String creator) {
		Integer generalParentId = 0;
		if(StringUtil.isEmpty(param.getAgentName())) {
			return Response.fail("请输入代理名称");
		}
		if(this.lambdaQuery().eq(AgentInfo::getAgentName, param.getAgentName()).count() > 0) {
			return Response.fail("代理名称已存在，请输入");
		}
		AgentInfo parent = null;
		if(param.getParentId() != null && param.getParentId() > 0) {
			parent = this.getById(param.getParentId());
			if(parent == null) {
				return Response.fail("上级代理信息错误");
			}
			generalParentId = parent.getParentId() == 0 ? parent.getId() : parent.getGeneralParentId();
		}
		AgentInfo agentInfo = new AgentInfo();
		agentInfo.setParentId(param.getParentId());
		agentInfo.setGeneralParentId(generalParentId);
		agentInfo.setAgentName(param.getAgentName());
		agentInfo.setCreator(creator);
		agentInfo.setServiceUrl(param.getServiceUrl());
		agentInfo.insert();
		if(agentInfo.getParentId() > 0) {
			AgentParentInfo api = new AgentParentInfo();
			api.setAgentId(agentInfo.getParentId());
			api.setMemberAgentId(agentInfo.getId());
			api.setMemberParentId(agentInfo.getParentId());
			api.insert();
			if(parent.getParentId() > 0) {
				List<ChildAndParentVO> uaLst = this.userInfoService.pNextParentAgents(agentInfo.getParentId());
				uaLst.forEach(i-> {
					AgentParentInfo apii = new AgentParentInfo();
					apii.setAgentId(i.getChildId());
					apii.setMemberAgentId(agentInfo.getId());
					apii.setMemberParentId(agentInfo.getParentId());
					apii.insert();
				});
			}
		}
		return Response.success();
	}

	@Override
	public Response<Void> mamangerEditAgent(EditAgentParamVO param) {
		if(StringUtil.isEmpty(param.getAgentName())) {
			return Response.fail("请输入代理名称");
		}
		AgentInfo agentInfo = this.getById(param.getId());
		if(agentInfo == null) {
			return Response.fail("代理信息错误");
		}
		if(param.getParentId() != null && param.getParentId() > 0 && param.getParentId().equals(agentInfo.getId())) {
			return Response.fail("上级代理不能选择自己");
		}
		if(this.lambdaQuery().eq(AgentInfo::getAgentName, param.getAgentName()).ne(AgentInfo::getId, agentInfo.getId()).count() > 0) {
			return Response.fail("代理名称已存在，请输入");
		}
		agentInfo.setAgentName(param.getAgentName());
		agentInfo.setServiceUrl(param.getServiceUrl());
		agentInfo.updateById();
		return Response.success();
	}

}
