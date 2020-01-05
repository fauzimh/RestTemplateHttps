package com.azifu.resttemplatehttps.client.configs;

import com.azifu.resttemplatehttps.client.constants.ClientConstants;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class RestTemplateConfig {
    private static final Logger LOG = getLogger(RestTemplateConfig.class);

    @Autowired
    private Environment env;

    @Bean
    @Autowired
    public RestTemplate getRestTemplate(HttpClient httpClient) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpComponentsClientHttpRequestFactory requestFactory;
            requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            requestFactory.setReadTimeout(1000);
            requestFactory.setConnectTimeout(1000);
            restTemplate.setRequestFactory(requestFactory);
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            throw new BeanCreationException("RestTemplate", "Failed to create a RestTemplate", e);
        }
        return restTemplate;
    }

    @Bean
    public HttpClient getHttpClient() {
        try {
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            SSLConnectionSocketFactory socketFactory = getSslConnectionSocketFactory();
            if (socketFactory != null) {
                httpClientBuilder.setSSLSocketFactory(socketFactory);
                if (Arrays.asList(env.getActiveProfiles()).contains(ClientConstants.PROFILE_NOOP_HOSTNAME_VERIFIER)) {
                    httpClientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
                }
            }
            return httpClientBuilder.setMaxConnTotal(5)
                    .setMaxConnPerRoute(5)
                    .build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new BeanCreationException("HttpClient", "Failed to create a HttpClient", e);
        }
    }

    private SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        try {
            if (Arrays.asList(env.getActiveProfiles()).contains(ClientConstants.PROFILE_NOOP_HOSTNAME_VERIFIER)) {
                return new SSLConnectionSocketFactory(getSslContext(), NoopHostnameVerifier.INSTANCE);
            }
            else{
                return new SSLConnectionSocketFactory(getSslContext());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private SSLContext getSslContext() throws Exception {
        SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
        sslContextBuilder.loadKeyMaterial(getKeyStore(), env.getProperty("client.ssl.key-store-password").toCharArray());
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        sslContextBuilder.loadTrustMaterial(null, acceptingTrustStrategy);
        return sslContextBuilder.build();
    }

    private KeyStore getKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(env.getProperty("client.ssl.key-store-type"));
        InputStream inputStream = getInputStream(env.getProperty("client.ssl.key-store"));
        keyStore.load(inputStream, env.getProperty("client.ssl.key-store-password").toCharArray());
        return keyStore;
    }

    private InputStream getInputStream(String path) throws IOException {
        if (path.startsWith("classpath")) {
            return new ClassPathResource(path.substring("classpath:".length())).getInputStream();
        } else {
            return new FileInputStream(ResourceUtils.getFile(path));
        }
    }
}
