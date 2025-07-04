package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareSubscripWithZeroParamVO {
	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;
}
