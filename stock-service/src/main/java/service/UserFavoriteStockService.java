package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFavoriteStock;
import entity.common.Response;
import vo.server.AddFavoriteStockParamVO;

/**
 * <p>
 * 用户自选股票信息 服务类
 * </p>
 *
 * @author
 * @since 2024-12-09
 */
public interface UserFavoriteStockService extends IService<UserFavoriteStock> {
	
	void favoriteList(Page<UserFavoriteStock> page, Integer userId);
	
	/**
	 * 添加自选股票
	 * 
	 * @param userId
	 * @param param
	 * @return
	 */
	Response<Void> addStocks(Integer userId, List<AddFavoriteStockParamVO> param);
}
