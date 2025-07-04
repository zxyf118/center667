package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserNewShareSubscription;
import vo.manager.NewShareSubscriptionListVO;
import vo.manager.NewShareSubscriptionSearchParamVO;
import vo.server.UserNewShareSubscriptionListVO;

/**
 * <p>
 * 新股申购记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface UserNewShareSubscriptionMapper extends BaseMapper<UserNewShareSubscription> {
	
	Page<NewShareSubscriptionListVO> managerList(Page<NewShareSubscriptionListVO> page, NewShareSubscriptionSearchParamVO param);
	
	@Select("<script>SELECT  "
			+ "    id, "
			+ "    stock_code, "
			+ "    stock_name, "
			+ "    stock_type, "
			+ "    stock_plate, "
			+ "    subscription_amount, "
			+ "    bond, "
			+ "    buying_price, "
			+ "    purchase_quantity, "
			+ "    award_quantity, "
			+ "    subscription_status, "
			+ "    subscription_time, "
			+ "    subscription_type, "
			+ "    lever, "
			+ "    award_time, "
			+ "    payment_time, "
			+ "    transfer_time "
			+ "FROM "
			+ "    user_new_share_subscription where user_id=#{userId} <if test=\"subscriptionType != null \">and subscription_type=#{subscriptionType}</if>"
			+ " ORDER BY id DESC</script>")
	Page<UserNewShareSubscriptionListVO> userNewShareSubscriptionList(Page<UserNewShareSubscriptionListVO> page, @Param("userId") Integer userId, @Param("subscriptionType") Integer subscriptionType);

	/**
	 * 获取-用户新股认缴过期
	 * @return
	 */
	List<UserNewShareSubscription> getUserNewShareSubscriptionExpired();
}
