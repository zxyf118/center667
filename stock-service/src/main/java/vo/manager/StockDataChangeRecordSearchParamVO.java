package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDataChangeRecordSearchParamVO extends BasePage {
	
	@ApiModelProperty("股票id")
	private Integer stockId;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
	@ApiModelProperty("变更类型代码")
	private String stockDataChangeTypeCode;
	
	@ApiModelProperty("变更时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeStart;
	
	@ApiModelProperty("变更时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeEnd;
}
