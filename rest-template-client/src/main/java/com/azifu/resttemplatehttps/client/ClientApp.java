package com.azifu.resttemplatehttps.client;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
public class ClientApp implements CommandLineRunner {
    private static final Logger LOG = getLogger(ClientApp.class);

    public static void main( String[] args )
    {
        SpringApplication.run(ClientApp.class, args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        String response = restTemplate.getForObject("https://localhost", String.class);
        LOG.info("response from https = {}", response);

        String responseHttp = restTemplate.getForObject("http://localhost", String.class);
        LOG.info("response from http = {}", responseHttp);
    }
}
