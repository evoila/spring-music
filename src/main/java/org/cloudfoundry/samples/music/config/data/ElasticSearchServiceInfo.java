package org.cloudfoundry.samples.music.config.data;

import org.springframework.cloud.service.BaseServiceInfo;
import org.springframework.cloud.service.ServiceInfo;

@ServiceInfo.ServiceLabel("elasticsearch")
public class ElasticSearchServiceInfo extends BaseServiceInfo {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public ElasticSearchServiceInfo(String id, String host, int port, String username, String password) {
        super(id);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @ServiceProperty(category = "host")
    public String getHost() {
        return host;
    }

    @ServiceProperty(category = "port")
    public int getPort() {
        return port;
    }

    @ServiceProperty(category = "username")
    public String getUsername() {
        return username;
    }

    @ServiceProperty(category = "password")
    public String getPassword() {
        return password;
    }
}
