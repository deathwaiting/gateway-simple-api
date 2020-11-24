package com.m.ahmedgalaltask.service;

import com.m.ahmedgalaltask.controllers.dao.GatewayDao;
import com.m.ahmedgalaltask.controllers.dao.PeripheralDeviceDao;
import com.m.ahmedgalaltask.enums.DeviceStatus;
import com.m.ahmedgalaltask.exceptions.RunTimeBusinessException;
import com.m.ahmedgalaltask.presistence.repositories.GatewayRepository;
import com.m.ahmedgalaltask.presistence.entities.Gateway;
import com.m.ahmedgalaltask.presistence.entities.PeripheralDevices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GatewayServiceImpl implements GatewayService{

    private static final String IP_4_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    @Autowired
    private GatewayRepository gatewayRepo;

    @Override
    public List<GatewayDao> getGateways() {
        return gatewayRepo
                .findAll()
                .stream()
                .map(this::toGatewayDao)
                .collect(toList());
    }



    @Override
    public GatewayDao getGateway(String serial) {
        return gatewayRepo
                .findById(serial)
                .map(this::toGatewayDao)
                .orElseThrow(() -> new RunTimeBusinessException(NOT_FOUND, "No gateway exists with serial[%s]", serial));
    }



    @Override
    public void addGateway(GatewayDao gatewayDao) {
        validateGatewayDao(gatewayDao);
        var entity = toGatewayEntity(gatewayDao);
        gatewayRepo.save(entity);
    }



    @Override
    public void updateGateway(GatewayDao gatewayDao) {
        validateGatewayDao(gatewayDao);
        var serial = gatewayDao.getSerialNumber();
        gatewayRepo
            .findById(serial)
            .map(ent -> toGatewayEntity(gatewayDao, ent))
            .map(gatewayRepo::save)
            .orElseThrow(() -> new RunTimeBusinessException(NOT_FOUND, "No gateway exists with serial[%s]", serial));
    }



    private void validateGatewayDao(GatewayDao gatewayDao) {
        boolean anyIsNull =
                Arrays.asList(gatewayDao, gatewayDao.getName(), gatewayDao.getSerialNumber(), gatewayDao.getIp4Address())
                     .stream()
                    .anyMatch(Objects::isNull);
        boolean tooMuchDevices =
                    ofNullable(gatewayDao.getDevices())
                        .orElse(emptyList())
                        .size() > 10;
        if(anyIsNull){
            throw new RunTimeBusinessException(NOT_ACCEPTABLE, "Missing gateway data!");
        }
        if(tooMuchDevices){
            throw new RunTimeBusinessException(NOT_ACCEPTABLE, "A gateway cannot have more that 10 devices!");
        }
        validateIpAddress(gatewayDao.getIp4Address());
        validateDevices(gatewayDao.getDevices());
    }




    private void validateDevices(List<PeripheralDeviceDao> devices) {
        devices.forEach(this::validateDeviceDao);
    }




    private void validateDeviceDao(PeripheralDeviceDao dao) {
        boolean anyIsNull =
                List.of(dao, dao.getUid(), dao.getVendor())
                        .stream()
                        .anyMatch(Objects::isNull);
        if(anyIsNull){
            throw new RunTimeBusinessException(NOT_ACCEPTABLE, "Missing Peripheral device data!");
        }
    }


    private void validateIpAddress(String ip4) {
        if(!ip4.matches(IP_4_REGEX)){
            throw new RunTimeBusinessException(NOT_ACCEPTABLE, "Invalid IPv4 address[%s]!", ip4);
        }
    }


    private GatewayDao toGatewayDao(Gateway entity){
        var dao = new GatewayDao();
        var devicesDaoList =
                entity
                    .getDevices()
                    .stream()
                    .map(this::toDeviceDao)
                    .collect(toList());
        dao.setIp4Address(entity.getIp4Address());
        dao.setName(entity.getName());
        dao.setSerialNumber(entity.getSerialNumber());
        dao.setDevices(devicesDaoList);
        return dao;
    }



    private Gateway toGatewayEntity(GatewayDao dao, Gateway entity){
        entity.setIp4Address(dao.getIp4Address());
        entity.setName(dao.getName());
        entity.setSerialNumber(dao.getSerialNumber());
        addDevicesToEntity(dao, entity);
        return entity;
    }




    private Gateway toGatewayEntity(GatewayDao dao){
        var entity = new Gateway();
        return toGatewayEntity(dao, entity);
    }




    private void addDevicesToEntity(GatewayDao dao, Gateway entity) {
        entity.getDevices().forEach(entity::removeDevice);
        ofNullable(dao.getDevices())
            .orElse(emptyList())
            .stream()
            .map(this::toDeviceEntity)
            .forEach(entity::addDevice);
    }




    private PeripheralDeviceDao toDeviceDao(PeripheralDevices entity){
        DeviceStatus status =
                ofNullable(entity.getStatus())
                .map(DeviceStatus::getByValue)
                .orElse(DeviceStatus.OFFLINE);
        var dao = new PeripheralDeviceDao();
        dao.setDateCreated(entity.getDateCreated());
        dao.setStatus(status);
        dao.setUid(entity.getUid());
        dao.setVendor(entity.getVendor());
        return dao;
    }



    private PeripheralDevices toDeviceEntity(PeripheralDeviceDao dao){
        Integer status = ofNullable(dao.getStatus()).map(DeviceStatus::getValue).orElse(null);
        var entity = new PeripheralDevices();
        entity.setDateCreated(dao.getDateCreated());
        entity.setStatus(status);
        entity.setUid(dao.getUid());
        entity.setVendor(dao.getVendor());
        return entity;
    }
}
