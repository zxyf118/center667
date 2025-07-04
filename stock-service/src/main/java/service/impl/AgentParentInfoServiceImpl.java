package service.impl;

import entity.AgentParentInfo;
import mapper.AgentParentInfoMapper;
import service.AgentParentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代理上下级关系表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-11-06
 */
@Service
public class AgentParentInfoServiceImpl extends ServiceImpl<AgentParentInfoMapper, AgentParentInfo> implements AgentParentInfoService {

}
