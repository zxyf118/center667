package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddStockParamVO {
	
	@ApiModelProperty(value = "股票名称")
    private String stockName;

    @ApiModelProperty(value = "股票代码")
    private String stockCode;
	
	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
    private String stockType;

    @ApiModelProperty(value = "板块名称，如：科创、创业")
    private String stockPlate;

    @ApiModelProperty(value = "是否锁定，锁定后无法交易")
    private Boolean isLock;

    @ApiModelProperty(value = "是否展示")
    private Boolean isShow;
	
	@ApiModelProperty(value = "大宗交易开关，0-关闭，1-开启")
    private Boolean isBlockTrading;

    @ApiModelProperty(value = "大宗交易增发价格")
    private BigDecimal blockTradingPrice;
	
	@ApiModelProperty(value = "大宗交易增发数量")
    private Integer blockTradingNum;
	
	@ApiModelProperty(value = "大宗交易最低买进股数")
    private Integer blockTradingBuyingMinNum;

    @ApiModelProperty(value = "锁仓T+N")
    private Integer lockInPeriod;
    
    @ApiModelProperty(value = "点差费率")
    private BigDecimal spreadRate;
    
    @ApiModelProperty(value = "大宗交易验证口令")
    private String blockTradingPwd;
}
