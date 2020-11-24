package com.m.ahmedgalaltask.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;

public enum DeviceStatus {
    ONLINE(0), OFFLINE(1);

    @Getter
    private final Integer value;

    DeviceStatus(Integer val){
        this.value = val;
    }


    public static DeviceStatus getByValue(Integer val){
        return Arrays
                .stream(DeviceStatus.values())
                .filter(status -> Objects.equals(status.value, val))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(format("No device status exists with value[%d]", val)));
    }


    public static DeviceStatus getByName(String name){
        return Arrays
                .stream(DeviceStatus.values())
                .filter(status -> Objects.equals(status.name(), name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(format("No device status exists with name[%s]", name)));
    }
}
