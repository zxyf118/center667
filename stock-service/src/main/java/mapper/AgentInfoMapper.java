package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.AgentInfo;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface AgentInfoMapper extends BaseMapper<AgentInfo> {
	Page<AgentListVO> managerAgentList(Page<AgentListVO> page, @Param("param") AgentListSearchParamVO param);
}
