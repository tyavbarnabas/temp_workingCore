package com.kenpb.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kenpb", exclude = {
    // SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
})
public class AppStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppStarter.class, args);
    }

}
