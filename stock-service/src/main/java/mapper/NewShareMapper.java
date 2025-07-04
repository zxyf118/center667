package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.NewShare;
import vo.manager.ServerNewShareSearchParamVO;
import vo.server.ServerNewShareVO;

/**
 * <p>
 * 新股表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface NewShareMapper extends BaseMapper<NewShare> {
	Page<ServerNewShareVO> getNewShareServerNewShareVO(Page<ServerNewShareVO> page, 
			@Param("param") ServerNewShareSearchParamVO param);
}
