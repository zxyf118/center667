package vo.server;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDayHotRankVO {
	
	@ApiModelProperty(value = "数据列表")
	private List<StockData> list = new ArrayList<>();
}
