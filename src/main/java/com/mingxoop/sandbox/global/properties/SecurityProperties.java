package com.mingxoop.sandbox.global.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@ConfigurationProperties(prefix = "security")
@ConfigurationPropertiesBinding
public class SecurityProperties {
	private final List<String> permitEndpoints;
}