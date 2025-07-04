package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareVO {

	@ApiModelProperty(value = "新股ID")
	private Integer id;

	@ApiModelProperty(value = "新股名称")
	private String stockName;

	@ApiModelProperty(value = "申购代码")
	private String stockCode;

	@ApiModelProperty(value = "类型")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "新股定价")
	private BigDecimal price;

	@ApiModelProperty(value = "发行总数")
    private Integer issueShares;
	
	@ApiModelProperty(value = "折扣价格")
	private BigDecimal discountedPrice;

	@ApiModelProperty(value = "最大申购股数")
	private Integer maxBuyingShares;

	@ApiModelProperty(value = "是否显示")
	private Boolean isShow;

	@ApiModelProperty(value = "是否锁定")
	private Boolean isLock;

	@ApiModelProperty(value = "申购截止日期")
	private Date subscriptionDeadline;

	@ApiModelProperty(value = "认缴截止日期")
	private Date paymentDeadline;

	@ApiModelProperty(value = "上市时间")
	private Date listingDate;

	@ApiModelProperty(value = "是否开启0元购")
	private Boolean enableZeroSubscription;

	@ApiModelProperty(value = "是否开启现金申购")
	private Boolean enableCashSubscription;

	@ApiModelProperty(value = "是否开启融资申购")
	private Boolean enableFinancingSubscription;
}
