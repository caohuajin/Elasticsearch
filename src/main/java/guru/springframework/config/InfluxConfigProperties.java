package guru.springframework.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxConfigProperties {
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create();
    }
}
