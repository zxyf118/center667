package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentListSearchParamVO extends BasePage {
	
	@ApiModelProperty("总代理Id，传null或0不生效")
	private Integer generalParentId;
	
	@ApiModelProperty("上级代理Id，传null或0不生效")
	private Integer parentId;
	
	@ApiModelProperty("代理Id")
	private Integer agentId;
	
	@ApiModelProperty("代理名称")
	private String agentName;

	@ApiModelProperty("注册时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeStart;
	
	@ApiModelProperty("注册时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeEnd;
	
}
