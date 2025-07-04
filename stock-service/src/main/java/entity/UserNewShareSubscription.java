package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 新股申购记录表
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_new_share_subscription")
@ApiModel(value="UserNewShareSubscription对象", description="新股申购记录表")
public class UserNewShareSubscription extends Model<UserNewShareSubscription> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

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
    
    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
	private BigDecimal exchangeRate;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
