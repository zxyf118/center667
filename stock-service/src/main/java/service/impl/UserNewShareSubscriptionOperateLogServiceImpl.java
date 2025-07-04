package service.impl;

import entity.UserNewShareSubscriptionOperateLog;
import mapper.UserNewShareSubscriptionOperateLogMapper;
import service.UserNewShareSubscriptionOperateLogService;
import vo.manager.NewShareSubscriptionOperateLogSearchParamVO;
import vo.manager.NewShareSubscriptionOperateLogVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
@Service
public class UserNewShareSubscriptionOperateLogServiceImpl extends ServiceImpl<UserNewShareSubscriptionOperateLogMapper, UserNewShareSubscriptionOperateLog> implements UserNewShareSubscriptionOperateLogService {
	@Resource
	private UserNewShareSubscriptionOperateLogMapper userNewShareSubscriptionOperateLogMapper;
	
	@Override
	public void managerList(Page<NewShareSubscriptionOperateLogVO> page, NewShareSubscriptionOperateLogSearchParamVO param) {
		userNewShareSubscriptionOperateLogMapper.managerList(page, param);
	}

}
