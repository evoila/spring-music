package org.cloudfoundry.samples.music.config.data;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq-cloud")
public class RabbitmqCloudConfig extends AbstractCloudConfig {

    @Bean
    public ConnectionFactory rabbitmqConnection() {
        return connectionFactory().rabbitConnectionFactory();
    }

}
