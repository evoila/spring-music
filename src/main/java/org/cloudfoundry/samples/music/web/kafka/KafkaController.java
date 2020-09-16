package org.cloudfoundry.samples.music.web.kafka;

import org.cloudfoundry.samples.music.model.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("kafka")
@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    private KafkaSender kafkaSender;

    @Autowired
    public void setKafkaSender(KafkaSender kafkaSender){
        this.kafkaSender = kafkaSender;
    }

    @PutMapping(value = "/send")
    public String producer(@RequestParam("message") String msg) {

        kafkaSender.send(msg + " " + msg);
        return "Message - " + msg + " sent to the Kafka Successfully";
    }
}
