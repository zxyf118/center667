package mapper;

import entity.UserFinancingInterestDayDetail;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-12-28
 */
public interface UserFinancingInterestDayDetailMapper extends BaseMapper<UserFinancingInterestDayDetail> {

	/**
	 * 获取用户所产生的所有利息
	 * @param userId
	 * @return
	 */
	BigDecimal getInterestGeneratedByUserId(Integer userId);

}
