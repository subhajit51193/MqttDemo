package com.mqtt.app.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * This configuration sets up a bidirectional communication channel with an MQTT broker using Spring Integration,
 * allowing the application to both receive messages from and send messages to MQTT topics.
 * @author SubhajitSaha
 *
 */
@Configuration
public class MqttBeans {

	private static final Logger logger = LoggerFactory.getLogger(MqttBeans.class);
	/**
	 * This method creates and configures a DefaultMqttPahoClientFactory instance, 
	 * which is responsible for creating MQTT client instances. It sets the MQTT connection 
	 * options such as server URI, username, password, and clean session.
	 * 
	 * @return MqttPahoClientFactory
	 */
	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		MqttConnectOptions options = new MqttConnectOptions();
		
		options.setServerURIs(new String[] {"tcp://localhost:1883"});
//		options.setUserName("admin");
//		String pass = "12345678";
//		options.setPassword(pass.toCharArray());
		options.setCleanSession(true);
		
		factory.setConnectionOptions(options);
		
		return factory;
	}
	
	/**
	 * This method creates a DirectChannel, which is a simple implementation of MessageChannel. 
	 * It serves as the input channel for messages arriving from the MQTT broker.
	 * 
	 * @return MessageProducer
	 */
	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}
	
	/**
	 * This method configures an MqttPahoMessageDrivenChannelAdapter, which acts as a message-driven 
	 * inbound endpoint for MQTT messages. It listens to a specific topic on the MQTT broker 
	 * (specified by "#") and forwards received messages to the mqttInputChannel
	 * 
	 * @return MessageProducer
	 */
	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn", mqttClientFactory(), "#");
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(2);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
		
	}
	
	/**
	 * This method defines a MessageHandler implementation, which handles incoming messages 
	 * from the mqttInputChannel. It extracts the topic from the message headers and prints it. 
	 * If the topic is "myTopic", it prints a specific message. It also prints the payload of the message.
	 * 
	 * @return MessageHandler
	 */
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {
			
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				
				String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
//				System.out.println("Received message on topic: " + topic);
				 logger.info("Received message on topic: {}", topic);
				if (topic.equals("myTopic")) {
					System.out.println("This is our topic");
				}
//				System.out.println("payLoad"+message.getPayload());
				logger.info("Payload: {}", message.getPayload());
			}
		};
	}
	
	/**
	 * This method creates another DirectChannel, which serves as the output channel 
	 * for messages that will be sent to the MQTT broker.
	 * 
	 * @return MessageChannel
	 */
	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}
	
	/**
	 * This method configures an MqttPahoMessageHandler, which acts as a message handler 
	 * for outbound messages. It sends messages to the MQTT broker with the specified 
	 * client ID ("serverOut") using the mqttOutboundChannel. It's set to send messages 
	 * asynchronously and publishes messages to the default topic ("#")
	 * 
	 * @return MessageHandler
	 */
	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("#");
		return messageHandler;
	}
	
	@Bean
	public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn", mqttClientFactory(), "#");
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
	    adapter.setQos(2);
	    adapter.setOutputChannel(mqttInputChannel());
	    return adapter;
	}
	
}
