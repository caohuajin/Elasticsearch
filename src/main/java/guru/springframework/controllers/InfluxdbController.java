package guru.springframework.controllers;


import guru.springframework.api.CommonResult;
import guru.springframework.domain.MyMeasurement;
import guru.springframework.services.InfluxdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("influxdb")
public class InfluxdbController {

    private static final Logger logger = LoggerFactory.getLogger(InfluxdbController.class);

    @Autowired
    private InfluxdbService influxdbService;

    @RequestMapping("/addMyMeasurement")
    public CommonResult addMyMeasurement() {
        influxdbService.addMyMeasurement();
        return CommonResult.success(null);
    }
    @RequestMapping("/getMyMeasurements")
    public CommonResult getMyMeasurements() {
        List<MyMeasurement> myMeasurements = influxdbService.getMyMeasurements();
        return CommonResult.success(myMeasurements);
    }
    /**
     * 删除数据
     */
    @RequestMapping("/deleteMyMeasurements")
    public CommonResult deleteMyMeasurements() {
        influxdbService.deleteMyMeasurement();
        return CommonResult.success(null);
    }


}

