package com.fabiankevin.reactive.reactiverest;

import com.fabiankevin.reactive.reactiverest.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Slf4j
public class UserController {
    private final UserRepository userRepository;
    private final KafkaService kafkaService;
    private final KafkaSender<String, String> kafkaSender;

    @GetMapping("/{id}")
    Mono<User> getUserById(@PathVariable Long id){
        return Mono.empty();
    }


    @PostMapping
    Mono<User> create(@RequestBody User user) {
        return userRepository.save(user)
                .doOnNext(user1 ->
                        kafkaSender.send(Mono.just(
                                SenderRecord.create(new ProducerRecord<>("love", user.toString()), user1.getId())
                        ))
                                .then()
                                .doOnError(e -> e.printStackTrace())
                                .doOnSuccess(s -> System.out.println("Sent!"))
                                .subscribe(res -> {
                    System.out.println(res);
                }));
    }


    @Scheduled(fixedDelay = 5000)
    public void mockingSendingOfData(){
        System.out.println("Automatic sending....");
        Random r = new Random();

        kafkaSender.send(
                Mono.just(SenderRecord.create(new ProducerRecord<>("test", User.builder()
                        .birthDate(LocalDate.now().minusDays(r.nextInt() * 10))
                        .name("Zoe")
                        .id(UUID.randomUUID().toString())
                        .build()
                        .toString()), "sad")))
                .then()
                .doOnSuccess(s -> System.out.println("You've sent a message."))
                .subscribe();
    }


    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> getAll() {

        return kafkaService.getEventPublisher()
                .map(consumerRecord -> ServerSentEvent.builder(consumerRecord.data()).build().toString());
    }
}
