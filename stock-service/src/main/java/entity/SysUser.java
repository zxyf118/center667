package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * 管理系统用户表
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
@ApiModel(value="SysUser对象", description="管理系统用户表")
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户名称")
    private String realName;

    @JsonIgnore
    @ApiModelProperty(value = "登录密码")
    private String loginPwd;

    @ApiModelProperty(value = "谷歌验证码，OPT验证码")
    private String googleKey;
    
    @ApiModelProperty("谷歌验证码二维码数据，为空则不用生成二维码")
    private String googleAuthQrCodeData;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "用户状态（0：停用，1：启用）")
    private Integer userStatus;

    public String getOperator() {
     	 return this.username + "(管理员)";
     }
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
