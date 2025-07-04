package service.impl;

import entity.UserFinancingInterestDayDetail;
import mapper.UserFinancingInterestDayDetailMapper;
import mapper.UserStockClosingPositionMapper;
import service.UserFinancingInterestDayDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-12-28
 */
@Service
public class UserFinancingInterestDayDetailServiceImpl extends ServiceImpl<UserFinancingInterestDayDetailMapper, UserFinancingInterestDayDetail> implements UserFinancingInterestDayDetailService {

	@Resource
	private UserFinancingInterestDayDetailMapper userFinancingInterestDayDetailMapper;
	
	/**
	 * 获取用户所产生的所有利息
	 */
	@Override
	public BigDecimal getInterestGeneratedByUserId(Integer userId) {
		return userFinancingInterestDayDetailMapper.getInterestGeneratedByUserId(userId);
	}

}
