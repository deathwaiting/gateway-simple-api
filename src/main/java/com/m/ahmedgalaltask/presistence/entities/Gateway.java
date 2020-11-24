package com.m.ahmedgalaltask.presistence.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "gateway")
public class Gateway {

    @Id
    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name= "name")
    private String name;

    @Column(name="ip4_address")
    private String ip4Address;

    @OneToMany(fetch = LAZY, mappedBy = "gateway", cascade = ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PeripheralDevices> devices;


    public Gateway(){
        devices = new HashSet<>();
    }


    public void addDevice(PeripheralDevices device){
        this.devices.add(device);
        device.setGateway(this);
    }


    public void removeDevice(PeripheralDevices device){
        this.devices.remove(device);
        device.setGateway(null);
    }
}
