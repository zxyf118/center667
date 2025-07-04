package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscriptionOperateLogSearchParamVO extends BasePage {
	
	@ApiModelProperty(value = "申购记录id")
    private Integer subscriptionId;
	
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

	@ApiModelProperty(value = "操作状态：null-查全部,0、申购中，1、已中签，2、已认缴，3、转持仓，4、未中签")
	private Integer operateType;
	
	@ApiModelProperty(value = "申购方式，null-查全部,0-现金申购,1-融资申购")
	private Integer subscriptionType;
	
	@ApiModelProperty(value = "开始时间")
	private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
	private Date endTime;
}
