package guru.springframework.services;


import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import guru.springframework.domain.MyMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class InfluxdbService {

    private static final Logger logger = LoggerFactory.getLogger(InfluxdbService.class);

    @Autowired
    private InfluxDBClient influxDBClient;
    private  String bucket = "chj-influx";
    private  String org = "21";

    public void addMyMeasurement() {
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        // 1. 通过Influx 行协议的方法写入 writeRecord
        // 选择时间戳模式
        writeApiBlocking.writeRecord(WritePrecision.NS, "MyMeasurement,location=河南 temperature=34.2,humidity=99");


        // 2. 通过写入 Point点API
        Point point = Point.measurement("MyMeasurement")
                .addTag("location", "厦门")
                .addField("temperature", 32.12)
                .addField("humidity", 88.0);
        writeApiBlocking.writePoint(point);

        // 3. POJO的模式写入
        WriteApi writeApi = influxDBClient.makeWriteApi();
        MyMeasurement myMeasurement = new MyMeasurement();
        myMeasurement.setLocation("广州");
        myMeasurement.setTemperature(88.0);
        myMeasurement.setHumidity(77);
        writeApi.writeMeasurement(WritePrecision.NS,myMeasurement);

    }
    public List<MyMeasurement> getMyMeasurements() {
        String flux = "from(bucket: \"chj-influx\")\n" +
                "  |> range(start: -38h)\n" ;
        //List<MyMeasurement> myMeasurements = influxDBClient.getQueryApi().query(flux, MyMeasurement.class);
        List<MyMeasurement> myMeasurements = new ArrayList<>();
        List<FluxTable> results = influxDBClient.getQueryApi().query(flux);
        for (FluxTable table : results) {
            for (FluxRecord record : table.getRecords()) {
                MyMeasurement myMeasurement = new MyMeasurement();
                if (record.getField().equals("temperature")) {
                    myMeasurement.setTemperature(Double.parseDouble(record.getValue().toString()));
                }else{
                    continue;
                }
//                if (record.getField().equals("humidity")) {
//                    myMeasurement.setHumidity(Double.parseDouble(record.getValue().toString()));
//                }
                myMeasurement.setLocation(record.getValueByKey("location").toString());
                myMeasurement.setTime(record.getTime());
                myMeasurements.add(myMeasurement);
            }
        }
        return myMeasurements;
    }
    /*
     * 只能按tag删除
     */
    public void deleteMyMeasurement() {
        String predictSql = "location=\"河南\"";
        DeleteApi deleteApi = influxDBClient.getDeleteApi();
        deleteApi.delete(OffsetDateTime.now().minusHours(24),
                OffsetDateTime.now(),
                predictSql,
                bucket,
                org
        );
    }

}
