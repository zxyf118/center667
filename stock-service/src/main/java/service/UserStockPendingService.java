package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserStockPending;
import entity.common.Response;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.manager.PendingTransferDetailVO;
import vo.manager.TransferPositionSearchParamVO;
import vo.server.BuyBlockTradingStockStockParamVO;
import vo.server.BuyStockParamVO;
import vo.server.BuyingStockPageVO;
import vo.server.StockPendingDetailVO;
import vo.server.StockPendingListVO;

/**
 * <p>
 * 用户股票委托订单表 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-20
 */
public interface UserStockPendingService extends IService<UserStockPending> {
	
	/**
	 * 委托订单列表
	 * @param page
	 * @param param
	 */
	void managerPendingList(Page<PendingListVO> page, PendingListSearchParamVO param);
	
	/**
	 * 委托订单转持仓
	 * @param ids
	 * @param ip
	 * @param operator
	 */
	Response<Void> pendingTransferPosition(List<TransferPositionSearchParamVO> searchParamList, String ip, String operator);
	
	/**
	 * 撤销委托订单
	 * @param id
	 * @param userId
	 * @param ip
	 * @param operator
	 */
	Response<Void> cancel(Integer id, Integer userId, String ip, String operator);
	
	/**
	 * 拒绝委托订单
	 * @param ids
	 * @param ip
	 * @param operator
	 */
	Response<Void> reject(List<Integer> ids, String ip, String operator);
	
	Response<BuyingStockPageVO> buyingStockPage(Integer userId, String stockCode, String stockType);
	
	Response<Void> buyStock(Integer userId, BuyStockParamVO param, String ip);
	
	Response<Void> buyBlockTradingStock(Integer userId, BuyBlockTradingStockStockParamVO param, String ip);
	
	void pendingList(Page<StockPendingListVO> page, Integer userId, Integer positionStatus);
	
	Response<StockPendingDetailVO> pendingDetail(Integer id, Integer userId);

	/**
	 * 委托订单-转入详情
	 * @param id 委托单id
	 * @return
	 */
	Response<PendingTransferDetailVO> transferDetail(Integer id);
}
