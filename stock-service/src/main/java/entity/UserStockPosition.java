package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import constant.Constant;
import enums.StockTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import utils.StringUtil;

/**
 * <p>
 * 用户持仓信息表
 * </p>
 *
 * @author 
 * @since 2024-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_stock_position")
@ApiModel(value="UserStockPosition对象", description="用户持仓信息表")
public class UserStockPosition extends Model<UserStockPosition> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "持仓ID")
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

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;
    
    @ApiModelProperty(value = "转入持仓时间")
    private Date positionTime;

    @ApiModelProperty(value = "持仓状态，2-已成交（正式持仓），5-已平仓")
    private Integer positionStatus;

    @ApiModelProperty(value = "买入价格")
    private BigDecimal buyingPrice;

    @ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
    private Integer positionDirection;

    @ApiModelProperty(value = "买入(当前持有)股数")
    private Integer buyingShares;

    @ApiModelProperty(value = "杠杆倍数")
    private Integer lever;
    
    @ApiModelProperty(value = "买入手续费")
    private BigDecimal buyingFee;
    
    @ApiModelProperty(value = "买入印花税")
    private BigDecimal buyingStampDuty;

    @ApiModelProperty(value = "是否锁仓")
    private Boolean isLock;

    @ApiModelProperty(value = "锁仓原因描述")
    private String lockMsg;

    @ApiModelProperty(value = "是否为大宗交易")
    private Boolean isBlockTrading;

    @ApiModelProperty(value = "锁仓天数")
    private Integer lockInPeriod;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("持仓ID：").append(this.id);
    	sb.append("\n持仓类型：").append(this.positionType == Constant.ACCOUNT_TYPE_REAL ? "实盘" : "模拟");
    	sb.append("\n用户ID：").append(this.userId);
    	sb.append("\n持有股票：").append(this.stockName).append("(").append(this.stockCode).append(")(").append(StockTypeEnum.getNameByCode(this.stockType)).append(")");
    	sb.append("\n买入价格：").append(this.buyingPrice);
    	sb.append("\n持有股数：").append(this.buyingShares);
    	sb.append("\n是否为大宗交易：").append(this.isBlockTrading ? "是" : "否");
    	sb.append("\n买入手续费：").append(this.buyingFee);
    	sb.append("\n买入印花税：").append(this.buyingStampDuty);
    	sb.append("\n杠杆：").append(this.getLever());
    	sb.append("\n持仓状态：").append(this.positionStatus == 2 ? "持仓" : "已平仓");
    	if(this.lockInPeriod != null) {
    		sb.append("\n锁仓天数：").append(this.lockInPeriod);
    	}
    	sb.append("\n是否锁仓：").append(this.isLock ? "是" : "否");
    	if(!StringUtil.isEmpty(this.lockMsg)) {
    		sb.append("\n锁仓原因：").append(this.lockMsg);
    	}
    	return sb.toString();
    }
}
