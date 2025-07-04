package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserAmtChangeRecord;
import vo.manager.UserAmtChangeRecordSearchParamVO;

/**
 * <p>
 * 会员资金变更记录表 Mapper 接口
 * </p>
 *
 * @author
 * @since 2024-11-01
 */
public interface UserAmtChangeRecordMapper extends BaseMapper<UserAmtChangeRecord> {
	Page<UserAmtChangeRecord> managerList(Page<UserAmtChangeRecord> page,
			@Param("param") UserAmtChangeRecordSearchParamVO param);
}
