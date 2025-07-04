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
 * 新股资料变更记录
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("new_share_data_change_record")
@ApiModel(value="NewShareDataChangeRecord对象", description="新股资料变更记录")
public class NewShareDataChangeRecord extends Model<NewShareDataChangeRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "新股ID")
    private Integer newShareId;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "资料变更类型编码")
    private String dataChangeTypeCode;

    @ApiModelProperty(value = "资料变更类型名称")
    private String dataChangeTypeName;

    @ApiModelProperty(value = "变更前的内容")
    private String oldContent;

    @ApiModelProperty(value = "变更后的内容")
    private String newContent;

    @ApiModelProperty(value = "变更时间")
    private Date createTime;

    @ApiModelProperty(value = "变更人")
    private String operator;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "IP归属地")
    private String ipAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
