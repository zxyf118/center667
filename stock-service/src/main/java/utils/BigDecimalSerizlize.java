package utils;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalSerizlize extends JsonSerializer<BigDecimal>{

	@Override
	public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value != null && !"".equals(value)) {
            gen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP) + "");
        } else {
            gen.writeString(value + "");
        }
	}

}
