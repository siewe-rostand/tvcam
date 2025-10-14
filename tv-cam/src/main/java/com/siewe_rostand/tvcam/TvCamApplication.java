package com.siewe_rostand.tvcam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
@Slf4j
public class TvCamApplication {

    public static void main(String[] args) {
        SpringApplication.run(TvCamApplication.class, args);
    }

}
