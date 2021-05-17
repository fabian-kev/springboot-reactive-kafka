package com.fabiankevin.reactive.reactiverest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

	private final KafkaReceiver<String,String> kafkaReceiver;

	private ConnectableFlux<ServerSentEvent<String>> eventPublisher;

	@PostConstruct
	public void init() {
		eventPublisher = kafkaReceiver.receive()
				.map(consumerRecord -> ServerSentEvent.builder(consumerRecord.value()).build())
				.publish();

		eventPublisher.connect();

	}

	public ConnectableFlux<ServerSentEvent<String>> getEventPublisher() {
		return eventPublisher;
	}

}