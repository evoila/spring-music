package org.cloudfoundry.samples.music.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.cloudfoundry.samples.music.model.RabbitMQSender;
import org.dom4j.io.SAXEventRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Profile("rabbitmq")
@RestController
@RequestMapping(value = "/rabbitmq")
public class RabbitmqController {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final Logger logger = LoggerFactory.getLogger(RabbitmqController.class);

    RabbitMQSender rabbitMQSender;

    @Autowired
    public void setRabbitMQSender(RabbitMQSender rabbitMQSender){
        this.rabbitMQSender = rabbitMQSender;
    }


    @PutMapping(value = "/send")
    public String producer(@RequestParam("message") String msg) {

        rabbitMQSender.send(msg + " " + msg);
        return "Message - " + msg + " sent to the RabbitMQ Successfully";
    }

    @GetMapping(value = "/userInfo")
    public ResponseEntity<String> getUserInfo() {

        Cloud cloud = getCloud();
        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        serviceInfos.forEach(serviceInfo -> logger.info(serviceInfo.toString()));

        //TODO
        // implement Rest Template

        /*boolean tlsEnabled = false;
        HttpMethod method = HttpMethod.GET;
        String baseUrl = ""; // get from env
        String endpoint = "/api/whoami";
        String url = baseUrl + endpoint;
        String username = "";
        String password = "";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader(username, password));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.info("Requesting: " + url + " and method " + method.toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate template;

        if (tlsEnabled) {
            SSLContext sslcontext;
            try {
                sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                        new TrustSelfSignedStrategy()).build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                throw new RuntimeException();
            }

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1"}, null, new NoopHostnameVerifier());

            HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpclient);

            template = new RestTemplate(factory);
        } else {
            template = new RestTemplate();
        }

        ResponseEntity<String> responseEntity = template.exchange(url, method, entity, String.class);

        logger.info(responseEntity.getBody());

        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.CREATED);
    */
        return null;
    }

    private String buildAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));

        return "Basic " + new String(encodedAuth);
    }

    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }
}