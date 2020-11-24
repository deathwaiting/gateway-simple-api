package com.m.ahmedgalaltask.controllers.dao;


import com.m.ahmedgalaltask.enums.DeviceStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class PeripheralDeviceDao {
    private String uid;
    private String vendor;
    private ZonedDateTime dateCreated;
    private DeviceStatus status;


    public PeripheralDeviceDao(String uid, String vendor, ZonedDateTime dateCreated, DeviceStatus status){
        this.uid = uid;
        this.vendor = vendor;
        this.dateCreated = dateCreated;
        this.status = status;
    }
}
