package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscriptionSearchParamVO extends BasePage {

	@ApiModelProperty("申购订单号")
	private String orderSn;

	@ApiModelProperty(value = "用户ID")
	private Integer userId;

	@ApiModelProperty(value = "代理ID")
	private Integer agentId;

	@ApiModelProperty(value = "新股ID")
	private Integer newShareId;

	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;

	@ApiModelProperty(value = "申购状态：null-查全部,0、申购中，1、已中签，2、已认缴，3、转持仓，4、未中签")
	private Integer subscriptionStatus;

	@ApiModelProperty(value = "申购类型：0:0元申购 ,1:现金申购 ,2:融资申购")
	private Integer subscriptionType;

	@ApiModelProperty(value = "开始时间")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	private Date endTime;
}
