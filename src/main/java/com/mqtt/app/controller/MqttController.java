package com.mqtt.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mqtt.app.gateway.MqttGateway;

@RestController
public class MqttController {

	@Autowired
	private MqttGateway mqttGateway;
	
	@Autowired
	private MqttPahoMessageDrivenChannelAdapter adapter;
	
	@PostMapping("/sendMessage")
	public ResponseEntity<?> publish(@RequestBody String mqttMessage){
		
		try {
			JsonObject converObject = new Gson().fromJson(mqttMessage,JsonObject.class);
			mqttGateway.sendToMqtt(converObject.get("message").toString(),converObject.get("topic").toString());
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok("Fail");
		}
		
				
		
	}
	
	@PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String topicJson) {
        try {
            JsonObject topicObject = new Gson().fromJson(topicJson, JsonObject.class);
            String topic = topicObject.get("topic").getAsString();
            adapter.addTopic(topic);
            return ResponseEntity.ok("Subscribed to " + topic);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Subscription failed");
        }
    }
}
