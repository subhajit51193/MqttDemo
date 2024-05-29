package com.mqtt.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mqtt.app.entity.Device;
import com.mqtt.app.repository.DeviceRepository;

@Service
public class DeviceServiceImpl implements DeviceService{

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Override
	public Device registerDevice(Device device) {
		return deviceRepository.save(device);
	}

}
