package service;

import entity.UserFinancingInterestDayDetail;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-12-28
 */
public interface UserFinancingInterestDayDetailService extends IService<UserFinancingInterestDayDetail> {

	/**
	 * 获取用户所产生的所有利息
	 * @param userId
	 * @return
	 */
	BigDecimal getInterestGeneratedByUserId(Integer userId);

}
