package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscripWithFinancingParamVO extends NewShareSubscripWithZeroParamVO {

	@ApiModelProperty("申购金额")
	private  BigDecimal subscriptionAmount;
	
	@ApiModelProperty("融资杠杆")
	private Integer lever;
}
