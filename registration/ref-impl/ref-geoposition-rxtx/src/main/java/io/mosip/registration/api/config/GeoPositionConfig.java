package io.github.tf-govstack.registration.api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "io.github.tf-govstack.registration.geoposition.rxtx" })
@Configuration
public class GeoPositionConfig {

}
