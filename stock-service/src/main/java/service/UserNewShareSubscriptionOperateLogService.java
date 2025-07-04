package service;

import entity.UserNewShareSubscriptionOperateLog;
import vo.manager.NewShareSubscriptionOperateLogSearchParamVO;
import vo.manager.NewShareSubscriptionOperateLogVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface UserNewShareSubscriptionOperateLogService extends IService<UserNewShareSubscriptionOperateLog> {
	void managerList(Page<NewShareSubscriptionOperateLogVO> page, NewShareSubscriptionOperateLogSearchParamVO param);
}
