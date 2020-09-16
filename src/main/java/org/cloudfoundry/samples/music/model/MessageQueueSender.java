package org.cloudfoundry.samples.music.model;

import org.springframework.stereotype.Service;

@Service
public interface MessageQueueSender {

    void send(String test);
}
