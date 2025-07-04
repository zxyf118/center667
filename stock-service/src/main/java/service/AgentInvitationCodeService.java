package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.AgentInvitationCode;
import entity.common.Response;
import vo.manager.InvitationCodeListSearchParamVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface AgentInvitationCodeService extends IService<AgentInvitationCode> {
	void managerInvitationCodeList(Page<AgentInvitationCode> page, InvitationCodeListSearchParamVO param);
	Response<Void> managerAddInvitationCode(Integer agentId, String invitationCode, String remark, String creator);
	Response<Void> managerDelInvitationCode(Integer id);
}
