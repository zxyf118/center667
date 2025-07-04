package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.AgentInvitationCode;
import vo.manager.InvitationCodeListSearchParamVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface AgentInvitationCodeMapper extends BaseMapper<AgentInvitationCode> {
	Page<AgentInvitationCode> managerInvitationCodeList(Page<AgentInvitationCode> page, @Param("param") InvitationCodeListSearchParamVO param);
}
