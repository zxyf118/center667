package vo.manager;

import entity.AgentInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentListVO extends AgentInfo {
	
	@ApiModelProperty(value = "一级代理名称")
	private String generalParentName;
	
	@ApiModelProperty(value = "直属上级代理名称")
    private String parentName;
	
	@ApiModelProperty(value = "下级代理数量")
	private Integer childAgentCount;
	
	@ApiModelProperty(value = "会员人数")
	private Integer memberCount;
	
}
