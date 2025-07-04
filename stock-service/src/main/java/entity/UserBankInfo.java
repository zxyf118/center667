package entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
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
 * @since 2024-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_bank_info")
@ApiModel(value="UserBankInfo对象", description="会员银行卡信息表")
public class UserBankInfo extends Model<UserBankInfo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    @ApiModelProperty(value = "银行编码")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "支行地址")
    private String branchBankAddress;

    @ApiModelProperty(value = "账户姓名")
    private String realName;

    @ApiModelProperty(value = "银行卡号")
    private String cardNo;

    @ApiModelProperty(value = "绑卡时间")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public boolean equals(UserBankInfo b) {
    	if(this.id.equals(b.getId())
    			&& this.userId.equals(b.getUserId())
    			&& this.bankCode.equals(b.getBankCode())
    			&& this.bankName.equals(b.getBankName())
    			&& this.province.equals(b.getProvince())
    			&& this.city.equals(b.getCity())
    			&& this.branchBankAddress.equals(b.getBranchBankAddress())
    			&& this.realName.equals(b.getRealName())
    			&& this.cardNo.equals(b.getCardNo())
    			) {
    		return true;
    	}
    	return false;
    }
    
    public String toString() {
    	return "银行：" + this.bankName
    			+ "\n省份：" + this.province
    			+ "\n城市：" + this.city
    			+ "\n支行地址：" + this.branchBankAddress
    			+ "\n账户名：" + this.realName
    			+ "\n卡号：" + this.cardNo;
    }
}
