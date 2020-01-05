package com.azifu.resttemplatehttps.server.configs;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;


@Configuration
public class HttpServerConfig {
	private static final Logger LOG = getLogger(HttpServerConfig.class);

	@Value("${http.port}")
	private int httpPort;

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
		Connector httpConnector = getHttpConnector();
		if (httpConnector != null) {
			tomcatServletWebServerFactory.addAdditionalTomcatConnectors(httpConnector);
		}
		return tomcatServletWebServerFactory;
	}

	private Connector getHttpConnector() {
		try {
			Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
			connector.setScheme("http");
			connector.setPort(httpPort);
			return connector;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}
