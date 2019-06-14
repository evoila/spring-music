package org.cloudfoundry.samples.music.model;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("rabbitmq")
@Service
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("")
    private String exchange;

    @Value("")
    private String routingkey;

    public void send(String test) {
        rabbitTemplate.convertAndSend(exchange, routingkey, test);
        System.out.println("Send msg = " + test);

    }
}