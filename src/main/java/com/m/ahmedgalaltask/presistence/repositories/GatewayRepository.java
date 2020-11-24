package com.m.ahmedgalaltask.presistence.repositories;

import com.m.ahmedgalaltask.presistence.entities.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GatewayRepository extends JpaRepository<Gateway, String> {

    @Query("SELECT gateway FROM Gateway gateway " +
            " LEFT JOIN FETCH gateway.devices dev ")
    List<Gateway> findAll();


    @Query("SELECT gateway FROM Gateway gateway " +
            " LEFT JOIN FETCH gateway.devices dev " +
            " WHERE gateway.serialNumber = :serial")
    Optional<Gateway> findById(@Param("serial") String serial);
}
