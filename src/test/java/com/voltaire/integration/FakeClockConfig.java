package com.voltaire.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
@ActiveProfiles(profiles = "inmemorydb-test")
public class FakeClockConfig {

    @Bean
    @Primary
    public Clock clockFixed() {
        return Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }
}
