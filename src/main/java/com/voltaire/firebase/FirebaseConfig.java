package com.voltaire.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @SneakyThrows
    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp is already initialized");
            return;
        }

        GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
            log.info("Init from env variable");
        } catch (IOException e) {
            log.info("Init firebase credentials from private key json file");
            return;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);
    }

}
