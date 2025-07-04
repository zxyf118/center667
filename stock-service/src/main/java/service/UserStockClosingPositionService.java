package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserStockClosingPosition;
import entity.common.Response;
import vo.manager.StockClosingPositionListVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.server.SellingStockPageVO;
import vo.server.UserStockClosingPositionDetailVO;
import vo.server.UserStockClosingPositionListVO;

/**
 * <p>
 * 用户股票平仓订单 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-25
 */
public interface UserStockClosingPositionService extends IService<UserStockClosingPosition> {
	/**
	 * 股票平仓列表
	 * @param page
	 * @param param
	 */
	void managerStockClosingList(Page<StockClosingPositionListVO> page, StockPositionListSearchParamVO param, boolean isBlockTrading);
	
	void userStockClosingPositionList(Page<UserStockClosingPositionListVO> page, Integer userId);
	
	Response<UserStockClosingPositionDetailVO> userStockClosingPositionDetail(Integer id, Integer userId);
	
	Response<SellingStockPageVO> sellingStockPage(Integer id, Integer userId);
	
	Response<Void> sellStock(Integer id, Integer userId, Integer shares, String ip);
}
