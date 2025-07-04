package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareChangeParamVO extends BasePage {
	
	@ApiModelProperty("新股ID")
	private Integer newShareId;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
	@ApiModelProperty("变更类型代码")
	private String dataChangeTypeCode;
	
	@ApiModelProperty("开始时间")
	private Date startTime;
	
	@ApiModelProperty("结束时间")
	private Date endTime;
}
