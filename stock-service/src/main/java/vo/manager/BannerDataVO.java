package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BannerDataVO {
	
	@ApiModelProperty("在线人数")
	private int onlineUserCount;
	
	@ApiModelProperty("待办充值订单数")
	private int pendingCashinOrders;
	
	@ApiModelProperty("待办提现订单数")
	private int pendingCashoutOrders;
	
	@ApiModelProperty("委托持仓订单数")
	private int pendingPositionOrders;
	
	@ApiModelProperty("待办用户认证数")
	private int pendingUserAuthentications;
	
	@ApiModelProperty("待办融资认证数")
	private int pendingFinancingCertifications;
	
	@ApiModelProperty("待办新股申购订单数")
	private int pendingNewShareSubscriptions;
	
	@ApiModelProperty("当前北京时间戳（毫秒）")
	private long beijingTimestamp;
	
	@ApiModelProperty("当前纽约（美东）时间戳（毫秒）")
	private long newYorkTimestamp;
	
	@ApiModelProperty("当前服务器-时间戳（毫秒）")
	private long serviceTimestamp;
}
