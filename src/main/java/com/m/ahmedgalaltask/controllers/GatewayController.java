package com.m.ahmedgalaltask.controllers;


import com.m.ahmedgalaltask.controllers.dao.GatewayDao;
import com.m.ahmedgalaltask.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GatewayController {


    @Autowired
    private GatewayService gatewayService;


    @GetMapping("/gateway")
    public List<GatewayDao> getGateways(){
        return gatewayService.getGateways();
    }


    @GetMapping("/gateway/{serialNumber}")
    public GatewayDao getGateway(@PathVariable("serialNumber") String serialNumber){
        return gatewayService.getGateway(serialNumber);
    }



    @PostMapping(value = "/gateway", consumes = APPLICATION_JSON_VALUE)
    public void postGateway(@RequestBody GatewayDao gateway){
        gatewayService.addGateway(gateway);
    }


    @PutMapping(value = "/gateway", consumes = APPLICATION_JSON_VALUE)
    public void updateGateway(@RequestBody GatewayDao gateway){
        gatewayService.updateGateway(gateway);
    }
}
