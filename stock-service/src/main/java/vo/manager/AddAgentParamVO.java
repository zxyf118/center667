package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddAgentParamVO {
	
	 @ApiModelProperty(value = "代理名称")
	 private String agentName;
	    
	 @ApiModelProperty(value = "直属上级代理ID")
	 private Integer parentId;
	 
	 @ApiModelProperty("客服链接")
	 private String serviceUrl;
}
