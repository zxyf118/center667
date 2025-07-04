package entity;

import java.math.BigDecimal;
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
 * 股票每日行情表
 * </p>
 *
 * @author 
 * @since 2024-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("daily_stock_quotes")
@ApiModel(value="DailyStockQuotes对象", description="股票每日行情表")
public class DailyStockQuotes extends Model<DailyStockQuotes> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "股票ID")
    private Integer stockId;

    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;

    @ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "年月日")
    private String ymd;

    @ApiModelProperty(value = "时分秒")
    private String hm;

    @ApiModelProperty(value = "当前价格")
    private BigDecimal nowPrice;

    @ApiModelProperty(value = "涨幅比例")
    private BigDecimal percentageIncrease;

    @ApiModelProperty(value = "开盘价格")
    private BigDecimal openPrice;

    @ApiModelProperty(value = "昨日收盘价格")
    private BigDecimal prevClose;

    @ApiModelProperty(value = "成交额")
    private BigDecimal volume;

    @ApiModelProperty(value = "成交量")
    private BigDecimal turnover;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
