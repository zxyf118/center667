package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewShareListSearchParamVO extends BasePage {
	
	@ApiModelProperty("新股ID")
	private Integer id;
	
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
	
	@ApiModelProperty("添加时间 开始")
	private Date addTimeStart;
	
	@ApiModelProperty("添加时间 结束")
	private Date addTimeEnd;
	
	@ApiModelProperty("申购方式，0-全部，1-现金申购，2-融资申购")
	private Integer subscriptionType;
}
