package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InvitationCodeListSearchParamVO extends BasePage {
	
	@ApiModelProperty("代理ID")
	private Integer agentId;
	
	@ApiModelProperty("邀请码")
	private String invitationCode;
	
	@ApiModelProperty("变更时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeStart;
	
	@ApiModelProperty("变更时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeEnd;
}
