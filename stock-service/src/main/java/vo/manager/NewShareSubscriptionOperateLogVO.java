package vo.manager;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscriptionOperateLogVO {
	
	@ApiModelProperty(value = "申购记录id")
    private Integer subscriptionId;

    @ApiModelProperty(value = "申购记录订单号")
    private String orderSn;

    @ApiModelProperty(value = "用户id")
    private Integer userId;
	
    @ApiModelProperty(value = "代理id")
    private Integer agentId;
    
    @ApiModelProperty(value = "代理名称")
    private String agentName;
   
    @ApiModelProperty(value = "新股id")
    private Integer newShareId;
    
    @ApiModelProperty(value = "新股代码")
    private String stockCode;

    @ApiModelProperty(value = "新股名称")
    private String stockName;
    
    @ApiModelProperty(value = "类型")
    private String stockType;

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;

    @ApiModelProperty(value = "申购数量")
    private Integer purchaseQuantity;

    @ApiModelProperty(value = "中签数量")
    private Integer awardQuantity;

    @ApiModelProperty(value = "申购类型：0:0元申购 ,1:现金申购,2:融资申购")
    private Integer subscriptionType;

    @ApiModelProperty(value = "操作类型：0、申购中，1、已中签，2、已认缴，3、转持仓，4、未中签")
    private Integer operateType;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "IP地址详情")
    private String ipAddress;
}
