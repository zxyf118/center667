package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.AgentInfo;
import entity.common.Response;
import vo.manager.AddAgentParamVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.EditAgentParamVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface AgentInfoService extends IService<AgentInfo> {
	
	void managerAgentList(Page<AgentListVO> page, AgentListSearchParamVO param);
	
	Response<Void> mamangerAddAgent(AddAgentParamVO param, String creator);
	
	Response<Void> mamangerEditAgent(EditAgentParamVO param);
}
