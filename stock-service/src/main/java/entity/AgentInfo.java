package entity;

import java.io.Serializable;
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
 * 
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("agent_info")
@ApiModel(value="AgentInfo对象", description="代理信息表")
public class AgentInfo extends Model<AgentInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "代理名称")
    private String agentName;

    @ApiModelProperty(value = "一级代理ID")
    private Integer generalParentId;
    
    @ApiModelProperty(value = "直属上级代理ID")
    private Integer parentId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "创建者")
    private String creator;
    
    @ApiModelProperty(value = "客服链接")
    private String serviceUrl;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
