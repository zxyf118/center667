package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_new_share_subscription_operate_log")
@ApiModel(value="UserNewShareSubscriptionOperateLog对象", description="")
public class UserNewShareSubscriptionOperateLog extends Model<UserNewShareSubscriptionOperateLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "新股申购记录ID")
    private Integer subscriptionId;

    @ApiModelProperty(value = "操作类型：1、已中签，2、已认缴，3、转持仓，4、未中签")
    private Integer operateType;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "IP地址详情")
    private String ipAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
