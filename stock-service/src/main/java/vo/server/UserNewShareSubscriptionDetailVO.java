package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserNewShareSubscriptionDetailVO {

	@ApiModelProperty(value = "申购id")
	private Integer id;

	@ApiModelProperty(value = "订单号")
	private String orderSn;

	@ApiModelProperty(value = "新股代码")
	private String stockCode;

	@ApiModelProperty(value = "新股名称")
	private String stockName;

	@ApiModelProperty(value = "类型")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "申购金额")
	private BigDecimal subscriptionAmount;

	@ApiModelProperty(value = "保证金")
	private BigDecimal bond;

	@ApiModelProperty(value = "申购价格")
	private BigDecimal buyingPrice;

	@ApiModelProperty(value = "申购数量")
	private Integer purchaseQuantity;

	@ApiModelProperty(value = "中签数量")
	private Integer awardQuantity;

	@ApiModelProperty(value = "申购状态：0、申购中，1、已中签，2、已认缴，3、转持仓，4、未中签，5、已过期，6、已取消")
	private Integer subscriptionStatus;

	@ApiModelProperty(value = "申购时间")
	private Date subscriptionTime;

	@ApiModelProperty(value = "申购类型：0:0元申购 ,1:现金申购 ,2:融资申购")
	private Integer subscriptionType;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;

	@ApiModelProperty(value = "中签时间")
	private Date awardTime;

	@ApiModelProperty(value = "认缴时间")
	private Date paymentTime;

	@ApiModelProperty(value = "转入持仓时间")
	private Date transferTime;

	@ApiModelProperty(value = "认缴截止日期")
	private Date paymentDeadline;
	
	@ApiModelProperty(value = "认缴金额")
	private BigDecimal payAmount;
}
