package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2024-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_financing_interest_day_detail")
@ApiModel(value="UserFinancingInterestDayDetail对象", description="")
public class UserFinancingInterestDayDetail extends Model<UserFinancingInterestDayDetail> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "关联user_financing_interest_info.id")
    private Integer financingInterestId;

    @ApiModelProperty(value = "利息产生日期")
    @TableField("interest_accrual_date")
    private Date interestAccrualDate;

    @ApiModelProperty(value = "是否结算")
    private Boolean settlement;

    @ApiModelProperty(value = "产生的利息")
    @TableField("interest_generated")
    private BigDecimal interestGenerated;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
