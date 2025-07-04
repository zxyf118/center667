package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户融资认证信息
 * </p>
 *
 * @author 
 * @since 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_financing_certification")
@ApiModel(value="UserFinancingCertification对象", description="用户融资认证信息")
public class UserFinancingCertification extends Model<UserFinancingCertification> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "申请时间")
    private Date requestTime;

    @ApiModelProperty(value = "操作、审核时间")
    private Date operateTime;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "状态（1：审核中，2：已通过，3：不通过）")
    private Integer fundingStatus;

    @ApiModelProperty(value = "可用金额")
    @TableField(exist = false)
    private BigDecimal availableAmt;
    
    @ApiModelProperty(value = "交易中的冻结金额")
    @TableField(exist = false)
    private BigDecimal tradingFrozenAmt;

    @ApiModelProperty(value = "ipo在途资金")
    @TableField(exist = false)
    private BigDecimal ipoAmt;
    
    @ApiModelProperty(value = "可提现金额")
    @TableField(exist = false)
    private BigDecimal availableWithdrawalAmt;
    
    @ApiModelProperty(value = "滞留金")
    @TableField(exist = false)
    private BigDecimal detentionAmt;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
