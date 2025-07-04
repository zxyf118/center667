package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserNewShareSubscriptionOperateLog;
import vo.manager.NewShareSubscriptionOperateLogSearchParamVO;
import vo.manager.NewShareSubscriptionOperateLogVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface UserNewShareSubscriptionOperateLogMapper extends BaseMapper<UserNewShareSubscriptionOperateLog> {
	Page<NewShareSubscriptionOperateLogVO>  managerList(Page<NewShareSubscriptionOperateLogVO> page, @Param("param") NewShareSubscriptionOperateLogSearchParamVO param);
}
