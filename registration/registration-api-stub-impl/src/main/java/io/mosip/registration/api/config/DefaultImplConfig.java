package io.github.tf-govstack.registration.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = { "io.github.tf-govstack.registration.api.impl.scanner", "io.github.tf-govstack.registration.api.impl.gps" })
@EnableConfigurationProperties
@Configuration
public class DefaultImplConfig {


}