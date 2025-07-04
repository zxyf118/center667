package vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockKChartVO {

    @ApiModelProperty(value = "k线图")
    private String[] candlestickChart;
}
