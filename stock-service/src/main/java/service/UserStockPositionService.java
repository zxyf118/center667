package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserStockPosition;
import entity.common.Response;
import vo.manager.StockPositionListSearchParamVO;
import vo.manager.StockPositionListVO;
import vo.server.StockPositionDetailVO;
import vo.server.UserStockPositionListVO;

/**
 * <p>
 * 用户持仓信息表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-08
 */
public interface UserStockPositionService extends IService<UserStockPosition> {
	
	/**
	 * 股票持仓列表
	 * @param page
	 * @param param
	 */
	void managerStockPositionList(Page<StockPositionListVO> page, StockPositionListSearchParamVO param, boolean isBlockTrading);
	
	/**
	 * 锁仓
	 * @param id
	 * @param lockMsg
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> lock(Integer id, String lockMsg, String ip, String operator);
	/**
	 * 解锁
	 * @param id
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> unlock(Integer id, String ip, String operator);
	/**
	 * 修改锁仓天数
	 * @param id
	 * @param lockInPeriod
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> lockInPeriodEdit(Integer id, Integer lockInPeriod, String ip, String operator);
	/**
	 * 强制平仓
	 * @param id
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> managerClosePosition(Integer id, String ip, String operator);
	
	void stockPositionList(Page<UserStockPositionListVO> page, Integer userId, String stockType, String stockCode);
	
	Response<StockPositionDetailVO> stockPositionDetail(Integer id, Integer userId);
}
