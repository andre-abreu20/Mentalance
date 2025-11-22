package br.com.fiap.mentalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MentalanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentalanceApplication.class, args);
    }
}

