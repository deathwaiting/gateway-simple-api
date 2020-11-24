package com.m.ahmedgalaltask.controllers.dao;


import lombok.Data;

import java.util.List;

@Data
public class GatewayDao {
    private String serialNumber;
    private String name;
    private String ip4Address;
    private List<PeripheralDeviceDao> devices;
}
