package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscripWithCashParamVO extends NewShareSubscripWithZeroParamVO {
	
	@ApiModelProperty(value = "申购数量")
	private Integer purchaseQuantity;
}
