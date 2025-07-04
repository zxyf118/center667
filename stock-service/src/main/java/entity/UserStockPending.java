package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import constant.Constant;
import enums.StockPendingStatusEnum;
import enums.StockTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户股票委托订单表
 * </p>
 *
 * @author 
 * @since 2024-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_stock_pending")
@ApiModel(value="UserStockPending对象", description="用户股票委托订单表")
public class UserStockPending extends Model<UserStockPending> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "委托ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "持仓类型，实盘-0，模拟-1")
    private Integer positionType;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "股票所属板块")
    private String stockPlate;

    @ApiModelProperty(value = "买入时的交易订单号")
    private String tradingOrderSn;

    @ApiModelProperty(value = "委托时间")
    private Date pendingTime;

    @ApiModelProperty(value = "转入持仓时间")
    private Date positionTime;

    @ApiModelProperty(value = "持仓状态，0-待提交，1-已委托，2-已成交，3-已撤销，4-已拒绝，5-已过期")
    private Integer positionStatus;

    @ApiModelProperty(value = "买入价格")
    private BigDecimal buyingPrice;

    @ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
    private Integer positionDirection;

    @ApiModelProperty(value = "买入股数")
    private Integer buyingShares;

    @ApiModelProperty(value = "杠杆倍数")
    private Integer lever;

    @ApiModelProperty(value = "买入手续费")
    private BigDecimal buyingFee;
    
    @ApiModelProperty(value = "买入手续费率")
    private BigDecimal buyingFeeRate;

    @ApiModelProperty(value = "买入时的印花税")
    private BigDecimal buyingStampDuty;
    
    @ApiModelProperty(value = "买入时的印花税率")
    private BigDecimal buyingStampDutyRate;

    @ApiModelProperty(value = "是否为大宗交易")
    private Boolean isBlockTrading;

    @ApiModelProperty(value = "持仓id，转入持仓后关联")
    private Integer positionId;

    @ApiModelProperty(value = "人民币兑换该流水币种的汇率")
	private BigDecimal exchangeRate;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("委托ID：").append(this.id);
    	sb.append("\n持仓类型：").append(this.positionType == Constant.ACCOUNT_TYPE_REAL ? "实盘" : "模拟");
    	sb.append("\n用户ID：").append(this.userId);
    	sb.append("\n持有股票：").append(this.stockName).append("(").append(this.stockCode).append(")(").append(StockTypeEnum.getNameByCode(this.stockType)).append(")");
    	sb.append("\n买入价格：").append(this.buyingPrice);
    	sb.append("\n买入股数：").append(this.buyingShares);
    	sb.append("\n是否为大宗交易：").append(this.isBlockTrading ? "是" : "否");
    	sb.append("\n买入手续费：").append(this.buyingFee);
    	sb.append("\n买入印花税：").append(this.buyingStampDuty);
    	sb.append("\n杠杆：").append(this.lever);
    	sb.append("\n持仓状态：").append(StockPendingStatusEnum.getNameByCode(this.positionStatus));
    	return sb.toString();
    }

}
