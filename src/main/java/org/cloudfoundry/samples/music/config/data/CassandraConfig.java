package org.cloudfoundry.samples.music.config.data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;

import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix="spring.data.cassandra")
@Configuration
@Profile({"cassandra-cloud", "cassandra"})
public class CassandraConfig extends AbstractCassandraConfiguration {

    private String contactPoints;
    private int port;
    private String keyspace;

    /*
     * Provide a contact point to the configuration.
     */
    public String getContactPoints() {
        return contactPoints;
    }

    /*
     * Provide a keyspace name to the configuration.
     */
    public String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected List<String> getStartupScripts() {
        List<String> createKeySpace = new ArrayList<>();
        createKeySpace.add("CREATE KEYSPACE IF NOT EXISTS  "+ getKeyspaceName() +"  WITH replication = {"
                + " 'class': 'SimpleStrategy', "
                + " 'replication_factor': '1' "
                + "};");
        return createKeySpace;
    }

    /*
     * Used to scan over classes and look for a class @Table
     */
    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"org.cloudfoundry.samples.music"};
    }

    /*
     * Automatic creates Cassandra Tables
     */
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    public void setContactPoints(String contactPoints) {
        this.contactPoints = contactPoints;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public void setPort(int port) {
        this.port = port;
    }


}