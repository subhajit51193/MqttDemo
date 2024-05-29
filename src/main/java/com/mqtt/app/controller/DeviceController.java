package com.mqtt.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mqtt.app.entity.Device;
import com.mqtt.app.service.DeviceService;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

	@Autowired
	private DeviceService deviceService;
	
	@PostMapping("/register")
	public ResponseEntity<Device> registerDeviceHandler(@RequestBody Device device){
		Device res = deviceService.registerDevice(device);
		return new ResponseEntity<Device>(res,HttpStatus.CREATED);
	}
	
}
