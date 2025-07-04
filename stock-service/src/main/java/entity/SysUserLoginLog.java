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
 * 管理员登录日志表
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_login_log")
@ApiModel(value="SysUserLoginLog对象", description="管理员登录日志表")
public class SysUserLoginLog extends Model<SysUserLoginLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "管理员id")
    private Integer sysUserId;

    @ApiModelProperty(value = "管理员账号")
    private String sysUserName;
    
    @ApiModelProperty(value = "管理员名称")
    private String sysRealName;

    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    @ApiModelProperty(value = "登录IP")
    private String loginIp;

    @ApiModelProperty(value = "IP所在地")
    private String ipAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
