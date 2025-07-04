package vo.common;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockQuotesVO {
	
	@ApiModelProperty(value = "股票类型+股票代码")
	private String gid;
	
	@ApiModelProperty(value = "股票名称")
	private String stockName;
	
	@ApiModelProperty(value = "涨幅比例")
    private BigDecimal percentageIncrease = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "当前价格")
    private BigDecimal nowPrice = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "当天最高价格")
    private BigDecimal heightPrice = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "当天最低价格")
    private BigDecimal lowPrice = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "开盘价格")
    private BigDecimal openPrice = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "成交量")
    private BigDecimal volume = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "成交额")
    private BigDecimal turnover = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "昨日收盘价格")
    private BigDecimal prevClose = BigDecimal.ZERO;
    
}
