package io.github.tf-govstack.registration.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = { "io.github.tf-govstack.registration.ref.sarxos", "io.github.tf-govstack.registration.ref.morena" })
@Configuration
public class ScannerRefImplConfig {


}