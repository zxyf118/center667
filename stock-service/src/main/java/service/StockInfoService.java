package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.StockInfo;
import entity.common.Response;
import vo.manager.AddStockParamVO;
import vo.manager.EditStockParamVO;
import vo.manager.StockListSearchParamVO;
import vo.server.BlockTradingStockInfoVO;
import vo.server.BlockTradingStockListVO;
import vo.server.IndexHotRankVO;
import vo.server.StockData;
import vo.server.StockDetailVO;

/**
 * <p>
 * 股票产品信息表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-07
 */
public interface StockInfoService extends IService<StockInfo> {
	
	void managerStockList(Page<StockInfo> page, StockListSearchParamVO param);
	
	Response<Void> managerAddStock(AddStockParamVO param, String ip, String operator);
	
	Response<Void> managerEditStock(EditStockParamVO param, String ip, String operator);
	
	Response<Void> managerDeleteStock(Integer id, String ip, String operator);
	
	Response<IndexHotRankVO> getHotRankFromApi();
	
	/**
	 * 短期活跃股票
	 * @param pageNo
	 * @param pageSize
	 * @param stockMarketTypeCode 股票市场类型
	 * @return
	 */
	Page<StockData> getActiveStockListFromApi(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode);
	
	Response<Page<BlockTradingStockListVO>> getBlockTradingStockList(Integer pageNo, Integer pageSize);
	
	Response<BlockTradingStockInfoVO> getBlockTradingStockInfo(String stockCode, String stockType, String blockTradingPwd);

	
	Response<StockDetailVO> getStockDetail(Integer userId, String stockCode, String stockType);

	/**
	 * 获取股票涨幅排行榜数据
	 * @param pageNo 
	 * @param pageSize
	 * @param sortTerm 排序条件
	 * @param sortType 排序类型
	 * @param reidsKey 缓存key
	 * @return
	 */
	Page<StockData> getStockQuoteListPage(Integer pageNo, Integer pageSize, String sortTerm, String sortType,
			String reidsKey);

	/**
	 * 股票数据初始化
	 * @return
	 */
	Response<String> stockDataInitialize();
	
	/**
	 * 近1个月涨幅榜
	 * @param pageNo
	 * @param pageSize
	 * @param stockMarketTypeCode
	 * @return
	 */
	List<StockData> getIncreaseRateRank(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode);
}
