package service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.AgentInfo;
import entity.AgentInvitationCode;
import entity.UserInfo;
import entity.common.Response;
import mapper.AgentInvitationCodeMapper;
import service.AgentInfoService;
import service.AgentInvitationCodeService;
import service.UserInfoService;
import utils.StringUtil;
import vo.manager.InvitationCodeListSearchParamVO;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Service
public class AgentInvitationCodeServiceImpl extends ServiceImpl<AgentInvitationCodeMapper, AgentInvitationCode> implements AgentInvitationCodeService {

	@Resource
	private AgentInfoService agentInfoService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private AgentInvitationCodeMapper agentInvitationCodeMapper;
	
	@Override
	public void managerInvitationCodeList(Page<AgentInvitationCode> page, InvitationCodeListSearchParamVO param) {
		agentInvitationCodeMapper.managerInvitationCodeList(page, param);
	}
	
	@Override
	public Response<Void> managerAddInvitationCode(Integer agentId, String invitationCode, String remark, String creator) {
		if(agentInfoService.lambdaQuery().eq(AgentInfo::getId, agentId).count() == 0) {
			return Response.fail("代理信息错误");
		}
		if(this.lambdaQuery().eq(AgentInvitationCode::getInvitationCode, invitationCode).count() > 0) {
			return Response.fail("此邀请码已存在，请重新输入");
		}
		AgentInvitationCode aic = new AgentInvitationCode();
		aic.setAgentId(agentId);
		aic.setInvitationCode(invitationCode);
		aic.setRemark(remark);
		aic.setCreator(creator);
		aic.insert();
		return Response.success();
	}

	@Override
	public Response<Void> managerDelInvitationCode(Integer id) {
		AgentInvitationCode code = this.getById(id);
		if(code == null) {
			return Response.fail("邀请码信息不存在");
		}
		userInfoService.lambdaUpdate().eq(UserInfo::getRegInvitationCode, code.getInvitationCode()).set(UserInfo::getRegInvitationCode, "").update();
		code.deleteById();
		return Response.success();
	}
}
