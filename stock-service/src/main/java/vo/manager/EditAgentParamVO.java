package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditAgentParamVO {
	
	@ApiModelProperty(value = "代理ID")
	private Integer id;

	@ApiModelProperty(value = "代理名称")
	private String agentName;

	@ApiModelProperty(value = "上级代理id")
	private Integer parentId;
	
	@ApiModelProperty("客服链接")
	private String serviceUrl;
}
