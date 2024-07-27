package com.valr.orderbook.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Value("${server.port}")
    private int serverPort;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(serverPort);
    }
}