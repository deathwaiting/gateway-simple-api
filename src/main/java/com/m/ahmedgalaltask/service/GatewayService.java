package com.m.ahmedgalaltask.service;

import com.m.ahmedgalaltask.controllers.dao.GatewayDao;

import java.util.List;

public interface GatewayService {
    List<GatewayDao> getGateways();
    GatewayDao getGateway(String serial);
    void addGateway(GatewayDao gateway);
    void updateGateway(GatewayDao gateway);
}
