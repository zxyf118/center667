package mapper;

import entity.AgentParentInfo;
import vo.common.ChildAndParentVO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 代理上下级关系表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-06
 */
public interface AgentParentInfoMapper extends BaseMapper<AgentParentInfo> {
	@Select("select member_agent_id as childId, member_parent_id as parentId from agent_parent_info where agent_id=#{agentId} order by id")
	List<ChildAndParentVO> agentParentInfoList(@Param("agentId") Integer agentId);
}
