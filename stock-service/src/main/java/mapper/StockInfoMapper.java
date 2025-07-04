package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.StockInfo;
import vo.manager.StockListSearchParamVO;
import vo.server.BlockTradingStockListVO;
import vo.server.StockData;

/**
 * <p>
 * 股票产品信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-07
 */
public interface StockInfoMapper extends BaseMapper<StockInfo> {
	Page<StockInfo> managerStockList(Page<StockInfo> page, @Param("param") StockListSearchParamVO param);
	
	@Select("select stock_name,stock_code,stock_type,stock_plate,block_trading_price,block_trading_num,sold_block_trading_num from stock_info where is_block_trading=1 and is_show=1")
	Page<BlockTradingStockListVO> getBlockTradingStockList(Page<BlockTradingStockListVO> page);
	
	@Select("SELECT  "
			+ "    s.stock_code, "
			+ "    s.stock_name, "
			+ "    s.stock_type, "
			+ "    s.stock_plate, "
			+ "    (SELECT  "
			+ "            SUM(percentage_increase) "
			+ "        FROM "
			+ "            (SELECT  "
			+ "                q.percentage_increase "
			+ "            FROM "
			+ "                daily_stock_quotes q "
			+ "            WHERE "
			+ "                q.stock_id = s.id "
			+ "            ORDER BY id DESC "
			+ "            LIMIT 0 , #{days}) t) AS percentageIncrease "
			+ "FROM "
			+ "    stock_info s "
			+ "WHERE "
			+ "    stock_type IN (${stockType})"
			+ "ORDER BY percentageIncrease DESC "
			+ "LIMIT #{size} ")
	List<StockData> getIncreaseRateRankByStockTypeAndDays(@Param("stockType") String stockType, @Param("days") int days, @Param("size") int size);
}
