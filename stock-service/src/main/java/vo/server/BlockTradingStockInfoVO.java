package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlockTradingStockInfoVO {
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

	@ApiModelProperty(value = "大宗交易最低买进股数")
	private Integer blockTradingBuyingMinNum;

	@ApiModelProperty(value = "当前市场价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "买入手续费费率")
	private BigDecimal buyingFeeRate;

	@ApiModelProperty(value = "印花税费率")
	private BigDecimal buyingStampDutyRate;
	
	@ApiModelProperty(value = "大宗-市场状态，0-已收盘，1-午间休市，2-交易中")
	private Integer bulkStatus;
	
	@ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;
}
