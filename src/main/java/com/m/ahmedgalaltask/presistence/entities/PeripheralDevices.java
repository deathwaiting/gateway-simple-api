package com.m.ahmedgalaltask.presistence.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name= "peripheral_device")
public class PeripheralDevices {
    @Id
    @Column(name = "uid")
    private String uid;

    @Column(name = "vendor")
    private String vendor;

    @Column(name="date_created")
    @CreationTimestamp
    ZonedDateTime dateCreated;

    @Column(name="status")
    private Integer status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gateway_serial")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Gateway gateway;
}
