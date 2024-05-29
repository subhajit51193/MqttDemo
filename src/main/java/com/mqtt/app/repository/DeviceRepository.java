package com.mqtt.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mqtt.app.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String>{

}
