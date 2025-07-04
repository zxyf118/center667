package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlockTradingStockListVO {
	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;
	
	@ApiModelProperty(value = "大宗交易增发价格")
	private BigDecimal blockTradingPrice;

	@ApiModelProperty(value = "大宗交易增发数量")
	private Integer blockTradingNum;

	@ApiModelProperty(value = "已出售的大宗交易股数")
	private Integer soldBlockTradingNum;

	@ApiModelProperty(value = "当前市场价格")
	private BigDecimal nowPrice;
	
	@ApiModelProperty(value = "涨幅比例")
    private BigDecimal percentageIncrease;
}
