package com.fabiankevin.reactive.reactiverest.kafka;

import com.fabiankevin.reactive.reactiverest.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
	String bootstrapServers = "127.0.0.1:9092";
	String topic = "test";
	@Bean
	KafkaReceiver kafkaReceiver() {

		Map<String, Object> configProps = new HashMap<>();
		configProps.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		ReceiverOptions<Integer, String> receiverOptions =
				ReceiverOptions.<Integer, String>create(configProps)
						.subscription(Collections.singleton(topic));

		return KafkaReceiver.create(receiverOptions);
	}
//
	@Bean
	KafkaSender<String, String> kafkaSender() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		configProps.put(ProducerConfig.RETRIES_CONFIG, 10);

		configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "163850"); // 163KByte
		// wait x millis to collect scheduler data before sending
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, "100");

		configProps.put(ProducerConfig.ACKS_CONFIG, "1");
		configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

		SenderOptions<String, String> senderOptions =
				SenderOptions.<String, String>create(configProps)
						.maxInFlight(1024);

		return KafkaSender.create(senderOptions);
//		return new DefaultKafkaSender<>(ProducerFactory.INSTANCE,
//				SenderOptions.create(configProps));
	}


//	@Bean
//	public NewTopic topic(){
//		return TopicBuilder.name("kevin-topic").partitions(3).build();
//	}
}