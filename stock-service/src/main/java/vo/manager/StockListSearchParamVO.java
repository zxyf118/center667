package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockListSearchParamVO extends BasePage {
	
	@ApiModelProperty("股票id")
	private Integer stockId;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
	@ApiModelProperty("股票类型，sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;
	
	@ApiModelProperty("板块名称，如：科创、创业")
	private String stockPlate;
	
	@ApiModelProperty("是否显示，null-全部，true-显示,false-不显示")
	private Boolean isShow;
	
	@ApiModelProperty("是否锁定，null-全部，true-锁定,false-不锁定")
	private Boolean isLock;
	
	@ApiModelProperty("是否查询大宗，null-查全部，true-是,false-否")
	private Boolean isBlockTrading;
	
	@ApiModelProperty("添加时间 开始")
	private Date addTimeStart;
	
	@ApiModelProperty("添加时间 结束")
	private Date addTimeEnd;
}
