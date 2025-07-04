package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IndexHotRankVO {
	
	@ApiModelProperty("热点榜显示1")
	private StockData displayFirst;
	
	@ApiModelProperty("热点榜显示2")
	private StockData displaySecond;
	
	@ApiModelProperty("热点榜显示3")
	private StockData displayThird;
	
	@ApiModelProperty("A股热点列表")
	private StockDayHotRankVO marketAList;
	
	@ApiModelProperty("港股热点列表")
	private StockDayHotRankVO marketHkList;
	
	@ApiModelProperty("美股热点列表")
	private StockDayHotRankVO marketUsList;
}
