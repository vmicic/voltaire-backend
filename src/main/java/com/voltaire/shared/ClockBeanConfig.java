package com.voltaire.shared;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockBeanConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
