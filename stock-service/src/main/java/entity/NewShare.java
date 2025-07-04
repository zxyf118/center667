package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import enums.StockTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import utils.StringUtil;

/**
 * <p>
 * 新股表
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("new_share")
@ApiModel(value="NewShare对象", description="新股表")
public class NewShare extends Model<NewShare> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @ApiModelProperty(value = "新股名称")
    private String stockName;
    
    @ApiModelProperty(value = "新股代码")
    private String stockCode;

    @ApiModelProperty(value = "类型")
    private String stockType;

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;

    @ApiModelProperty(value = "新股定价")
    private BigDecimal price;
    
    @ApiModelProperty(value = "折扣价格")
    private BigDecimal discountedPrice;

    @ApiModelProperty(value = "最大申购股数")
    private Integer maxBuyingShares;
    
    @ApiModelProperty(value = "发行总数")
    private Integer issueShares;

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

    @ApiModelProperty(value = "添加时间")
    private Date addTime;
    
    @ApiModelProperty(value = "创建人")
    private String creator;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	sb.append("新股id：").append(this.id);
    	sb.append("\n名称：").append(this.stockName).append("(").append(this.stockCode).append(")");
    	sb.append("\n类型：").append(StockTypeEnum.getNameByCode(this.stockType));
    	if(!StringUtil.isEmpty(this.stockPlate)) {
    		sb.append("\n板块：").append(this.stockPlate);
    	}
    	sb.append("\n定价：").append(this.price);
    	sb.append("\n发行总数：").append(this.issueShares);
    	if(this.discountedPrice != null) {
    		sb.append("\n折扣价格：").append(this.discountedPrice);
    	}
    	sb.append("\n最大申购数量：").append(this.maxBuyingShares);
    	sb.append("\n是否锁定：").append(this.isLock ? "是" : "否");
    	sb.append("\n是否显示：").append(this.isShow ? "是" : "否");
    	if(this.subscriptionDeadline != null) {
    		sb.append("\n申购截止日期：").append(sdf.format(this.subscriptionDeadline));
    	}
    	if(this.paymentDeadline != null) {
    		sb.append("\n认缴截止日期：").append(sdf.format(this.paymentDeadline));
    	}
    	if(this.listingDate != null) {
    		sb.append("\n上市时间：").append(sdf.format(this.listingDate));
    	}
    	sb.append("\n是否开启0元申购：").append(this.enableZeroSubscription ? "是" : "否");
    	sb.append("\n是否开启现金申购：").append(this.enableCashSubscription ? "是" : "否");
    	sb.append("\n是否开启融资申购：").append(this.enableFinancingSubscription ? "是" : "否");
    	return sb.toString();
    }
}
