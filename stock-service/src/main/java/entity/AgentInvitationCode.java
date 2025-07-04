package entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("agent_invitation_code")
@ApiModel(value="AgentInvitationCode对象", description="代理邀请码表")
public class AgentInvitationCode extends Model<AgentInvitationCode> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "代理ID")
    private Integer agentId;
    
    @ApiModelProperty(value = "代理名称")
    @TableField(exist = false)
    private String agentName;

    @ApiModelProperty(value = "邀请码")
    private String invitationCode;

    @ApiModelProperty(value = "注册会员数量")
    private Integer spreadCount;
    
    @ApiModelProperty(value = "备注")
    private String remark;
    
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "创建者")
    private String creator;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
