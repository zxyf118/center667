package vo.manager;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServerNewShareSearchParamVO{

	@ApiModelProperty(value = "类型-包含")
    private String stockTypeIn;
	
	@ApiModelProperty(value = "开始时间")
	private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
	private Date endTime;
}
