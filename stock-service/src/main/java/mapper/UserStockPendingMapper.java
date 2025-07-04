package mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserStockPending;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.server.StockPendingListVO;

/**
 * <p>
 * 用户股票委托订单表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-12-20
 */
public interface UserStockPendingMapper extends BaseMapper<UserStockPending> {
	Page<PendingListVO> managerPendingList(Page<PendingListVO> page, @Param("param") PendingListSearchParamVO param);
	
	@Select("select id,stock_name,stock_code,stock_type,stock_plate,buying_shares,position_direction,lever,buying_price,position_status,is_block_trading,pending_time from user_stock_pending where user_id=#{userId} and position_status=#{positionStatus} ORDER BY pending_time DESC")
	Page<StockPendingListVO> pendingList(Page<StockPendingListVO> page, @Param("userId") Integer userId, @Param("positionStatus") Integer positionStatus);

	@Select("SELECT ifnull(sum(buying_shares), 0) FROM user_stock_pending p where p.position_id=#{positionId} and p.position_time>date_sub(sysdate(), interval #{lockInPeriod} day)")
	int getUnavailableShares(@Param("positionId") Integer positionId, @Param("lockInPeriod") Integer lockInPeriod);
}
