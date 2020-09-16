package org.cloudfoundry.samples.music.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.*;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.cloudfoundry.samples.music.config.Profiles.*;

public class SpringApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Log logger = LogFactory.getLog(SpringApplicationContextInitializer.class);

    private static final Map<Class<? extends ServiceInfo>, String> serviceTypeToProfileName = new HashMap<>();
    private static final List<String> validLocalProfiles = Arrays.asList(MYSQL, POSTGRES, SQLSERVER, ORACLE, MONGODB,
                    REDIS, RABBITMQ, CASSANDRA, ELASTICSEARCH, KAFKA);

    static {
        serviceTypeToProfileName.put(MongoServiceInfo.class, MONGODB);
        serviceTypeToProfileName.put(PostgresqlServiceInfo.class, POSTGRES);
        serviceTypeToProfileName.put(MysqlServiceInfo.class, MYSQL);
        serviceTypeToProfileName.put(RedisServiceInfo.class, REDIS);
        serviceTypeToProfileName.put(OracleServiceInfo.class, ORACLE);
        serviceTypeToProfileName.put(SqlServerServiceInfo.class, SQLSERVER);
        serviceTypeToProfileName.put(AmqpServiceInfo.class, RABBITMQ);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Cloud cloud = getCloud();

        ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();

        validateActiveProfiles(appEnvironment);

        addCloudProfile(cloud, appEnvironment);

        excludeAutoConfiguration(appEnvironment);
    }

    private void addCloudProfile(Cloud cloud, ConfigurableEnvironment appEnvironment) {
        if (cloud == null) {
            return;
        }

        List<String> profiles = new ArrayList<>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        logger.info("Found serviceInfos: " + StringUtils.collectionToCommaDelimitedString(serviceInfos));

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(serviceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "Only one service of the following types may be bound to this application: " +
                            serviceTypeToProfileName.values().toString() + ". " +
                            "These services are bound to the application: [" +
                            StringUtils.collectionToCommaDelimitedString(profiles) + "]");
        }

        if (profiles.size() > 0) {
            appEnvironment.addActiveProfile(profiles.get(0));
            appEnvironment.addActiveProfile(profiles.get(0) + "-cloud");
        }
    }

    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }

    private void validateActiveProfiles(ConfigurableEnvironment appEnvironment) {
        List<String> serviceProfiles = Stream.of(appEnvironment.getActiveProfiles())
                .filter(validLocalProfiles::contains)
                .collect(Collectors.toList());

        if (serviceProfiles.size() > 1) {
            throw new IllegalStateException("Only one active Spring profile may be set among the following: " +
                    validLocalProfiles.toString() + ". " +
                    "These profiles are active: [" +
                    StringUtils.collectionToCommaDelimitedString(serviceProfiles) + "]");
        }
    }

    private void excludeAutoConfiguration(ConfigurableEnvironment environment) {
        List<String> exclude = new ArrayList<>();

        if (environment.acceptsProfiles(Profiles.of(REDIS))) {
            excludeDataSourceAutoConfiguration(exclude);
            excludeMongoAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        } else if (environment.acceptsProfiles(Profiles.of(MONGODB))) {
            excludeDataSourceAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        } else if (environment.acceptsProfiles(Profiles.of(RABBITMQ))) {
            excludeMongoAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeDataSourceAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        } else if (environment.acceptsProfiles(Profiles.of(CASSANDRA))) {
            excludeMongoAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeDataSourceAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        } else if (environment.acceptsProfiles(Profiles.of(ELASTICSEARCH))) {
            excludeMongoAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeDataSourceAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        } else if (environment.acceptsProfiles(Profiles.of(KAFKA))) {
            excludeMongoAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeDataSourceAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
        } else {
            excludeMongoAutoConfiguration(exclude);
            excludeRedisAutoConfiguration(exclude);
            excludeRabbitAutoConfiguration(exclude);
            excludeCassandraAutoConfiguration(exclude);
            excludeElasticsearchAutoConfiguration(exclude);
            excludeKafkaAutoConfiguration(exclude);
        }

        Map<String, Object> properties = Collections.singletonMap("spring.autoconfigure.exclude",
                StringUtils.collectionToCommaDelimitedString(exclude));

        PropertySource<?> propertySource = new MapPropertySource("springMusicAutoConfig", properties);

        environment.getPropertySources().addFirst(propertySource);
    }

    private void excludeDataSourceAutoConfiguration(List<String> exclude) {
        exclude.add(DataSourceAutoConfiguration.class.getName());
    }

    private void excludeMongoAutoConfiguration(List<String> exclude) {
        exclude.addAll(Arrays.asList(
                MongoAutoConfiguration.class.getName(),
                MongoDataAutoConfiguration.class.getName(),
                MongoRepositoriesAutoConfiguration.class.getName()
        ));
    }

    private void excludeRedisAutoConfiguration(List<String> exclude) {
        exclude.addAll(Arrays.asList(
                RedisAutoConfiguration.class.getName(),
                RedisRepositoriesAutoConfiguration.class.getName()
        ));
    }

    private void excludeRabbitAutoConfiguration(List<String> exclude) {
        exclude.addAll(Collections.singletonList(
                RabbitAutoConfiguration.class.getName()
        ));
    }

    private void excludeCassandraAutoConfiguration(List<String> exclude) {
        exclude.addAll(Arrays.asList(
                CassandraAutoConfiguration.class.getName(),
                CassandraDataAutoConfiguration.class.getName(),
                CassandraRepositoriesAutoConfiguration.class.getName()
        ));
    }

    private void excludeElasticsearchAutoConfiguration(List<String> exclude) {
        exclude.addAll(Arrays.asList(
                ElasticsearchRepositoriesAutoConfiguration.class.getName(),
                ElasticSearchRestHealthContributorAutoConfiguration.class.getName(),
                ElasticsearchDataAutoConfiguration.class.getName(),
                ElasticsearchRestClientAutoConfiguration.class.getName()
        ));
    }

    private void excludeKafkaAutoConfiguration(List<String> exclude) {
        exclude.addAll(Collections.singletonList(
                KafkaAutoConfiguration.class.getName()
        ));
    }
}
