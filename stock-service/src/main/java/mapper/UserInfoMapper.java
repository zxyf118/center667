package mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserInfo;
import vo.common.ChildAndParentVO;
import vo.manager.UserListSearchParamVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-10-11
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {
	
	List<ChildAndParentVO> pNextParentUsers(@Param("userId") Integer userId);
	
	List<ChildAndParentVO> pNextParentAgents(@Param("agentId") Integer agentId);
	
	Page<UserInfo> managerUserList(Page<UserInfo> page, @Param("param")UserListSearchParamVO vo, @Param("ids") Set<Integer> ids);
	
	@Select("SELECT  "
			+ "    (SELECT  "
			+ "            IFNULL(SUM(amount_received), 0) "
			+ "        FROM "
			+ "            user_stock_closing_position "
			+ "        WHERE "
			+ "            stock_type IN ('sh' , 'sz', 'bj', 'us') and user_id=#{userId}"
			+ "                AND transaction_status = 1 "
			+ "                AND transaction_time > DATE_SUB(SYSDATE(), INTERVAL 1 DAY)) + (SELECT  "
			+ "            IFNULL(SUM(amount_received), 0) "
			+ "        FROM "
			+ "            user_stock_closing_position "
			+ "        WHERE "
			+ "            stock_type = 'hk' and user_id=#{userId}"
			+ "                AND transaction_status = 1 "
			+ "                AND transaction_time > DATE_SUB(SYSDATE(), INTERVAL 2 DAY))")
	BigDecimal getUserUnavailableWithdrawalAmt(@Param("userId") Integer userId);
}
