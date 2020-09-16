package org.cloudfoundry.samples.music.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Profile("kafka")
@Service
public class KafkaSender implements MessageQueueSender {

    private KafkaTemplate<String, String> kafkaTemplate;

    private Logger log = LoggerFactory.getLogger(KafkaSender.class);

    private final CountDownLatch latch = new CountDownLatch(3);

    KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String test) {
        kafkaTemplate.send(TOPIC, test);
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Error while waiting", e);
        }
    }

    @KafkaListener(topics = TOPIC)
    public void listen(ConsumerRecord<String, String> cr) {
        log.info(cr.toString());
        latch.countDown();
    }

    private static final String TOPIC = "music";
}
