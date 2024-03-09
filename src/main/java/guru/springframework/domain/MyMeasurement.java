package guru.springframework.domain;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "MyMeasurement")
public class MyMeasurement {

    @Column(tag = true)
    private String location;

    /**
     * 温度
     */
    @Column
    private double temperature;

    /**
     * 湿度
     */
    @Column
    private double humidity;

    @Column(timestamp = true)
    Instant time;

}
