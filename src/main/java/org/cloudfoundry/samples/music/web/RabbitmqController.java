package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.model.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("rabbitmq")
@RestController
@RequestMapping(value = "/rabbitmq")
public class RabbitmqController {
    private static final Logger logger = LoggerFactory.getLogger(RabbitmqController.class);

    RabbitMQSender rabbitMQSender;

    @Autowired
    public void setRabbitMQSender(RabbitMQSender rabbitMQSender){
        this.rabbitMQSender = rabbitMQSender;
    }


    @GetMapping(value = "/produce")
    public String producer(@RequestParam("empName") String msg) {

        rabbitMQSender.send(msg + " " + msg);

        return "Message - " + msg + " sent to the RabbitMQ Successfully";
    }
}