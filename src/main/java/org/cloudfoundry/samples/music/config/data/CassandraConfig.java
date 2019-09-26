package org.cloudfoundry.samples.music.config.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;


@ConfigurationProperties("vcap.services.sm-cass.credentials")
@Configuration
@Profile({"cassandra-cloud", "cassandra"})
public class CassandraConfig extends AbstractCassandraConfiguration {

    private static final Log logger = LogFactory.getLog(CassandraConfig.class);

    private String keyspace;
    private String user;
    private String password;
    private String database;
    private String uri;
    /*
     * Provide a contact point to the configuration.
     */
    public String getContactPoints() {
        return getContactPointsFromUri(getUri());
    }

    /*
     * Provide a keyspace name to the configuration.
     */
    public String getKeyspaceName() {
        return getKeySpaceFromUri(getUri());
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {

        CassandraClusterFactoryBean bean = new CassandraClusterFactoryBean();

        bean.setAddressTranslator(getAddressTranslator());
        bean.setAuthProvider(getAuthProvider());
        bean.setClusterBuilderConfigurer(getClusterBuilderConfigurer());
        bean.setClusterName(getClusterName());
        bean.setCompressionType(getCompressionType());
        bean.setContactPoints(getContactPoints());
        bean.setLoadBalancingPolicy(getLoadBalancingPolicy());
        bean.setMaxSchemaAgreementWaitSeconds(getMaxSchemaAgreementWaitSeconds());
        bean.setMetricsEnabled(getMetricsEnabled());
        bean.setNettyOptions(getNettyOptions());
        bean.setPoolingOptions(getPoolingOptions());
        bean.setPort(getPort());
        bean.setProtocolVersion(getProtocolVersion());
        bean.setQueryOptions(getQueryOptions());
        bean.setReconnectionPolicy(getReconnectionPolicy());
        bean.setRetryPolicy(getRetryPolicy());
        bean.setSpeculativeExecutionPolicy(getSpeculativeExecutionPolicy());
        bean.setSocketOptions(getSocketOptions());
        bean.setTimestampGenerator(getTimestampGenerator());

        bean.setKeyspaceCreations(getKeyspaceCreations());
        bean.setKeyspaceDrops(getKeyspaceDrops());
        bean.setStartupScripts(getStartupScripts());
        bean.setShutdownScripts(getShutdownScripts());
        bean.setUsername(getUser());
        bean.setPassword(getPassword());
        return bean;
    }

    /** Uncomment this method if you test the application in a new Env such as
     * a Docker Container. This will create a KeySpace if it does not exist.
    @Override
    protected List<String> getStartupScripts() {
        List<String> createKeySpace = new ArrayList<>();
        createKeySpace.add("CREATE KEYSPACE IF NOT EXISTS  "+ getKeyspaceName() +"  WITH replication = {"
                + " 'class': 'SimpleStrategy', "
                + " 'replication_factor': '1' "
                + "};");
        return createKeySpace;
    }
    */

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

    private String getContactPointsFromUri(String uri) {
        String contactPointUri = uri.substring(12);
        String[] getHosts = contactPointUri.split("@");
        String hostAndDb = getHosts[1];
        String[] host = hostAndDb.split(":");

        String contactPoint = host[0];
        for (int i = 1; i < host.length-1; i++) {
            String portAndHost  = host[i];
            String[] split = portAndHost.split(",");
            contactPoint = contactPoint + "," + split[1];
        }
        logger.info(contactPoint);
        return contactPoint;
    }

    private String getKeySpaceFromUri(String uri) {
        String subUri = uri.substring(12);
        String[] split = subUri.split("/");
        String keyspace = split[1];
        logger.info(keyspace);

        return keyspace;

    }


    private int getPortFromUri(String uri) {
        String contactPointUri = uri.substring(12);
        String[] getHosts = contactPointUri.split("@");
        String hostAndDb = getHosts[1];
        String[] host = hostAndDb.split(":");

        String portAndHost  = host[1];
        String[] split = portAndHost.split(",");
        String port = split[0];

        logger.info(port);
        return Integer.parseInt(port);
    }

    @Override
    public int getPort() {
        return getPortFromUri(getUri());
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getUri() {
        return uri;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}