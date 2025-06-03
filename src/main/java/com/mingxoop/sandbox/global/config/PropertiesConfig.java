package com.mingxoop.sandbox.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.mingxoop.sandbox")
public class PropertiesConfig {
}