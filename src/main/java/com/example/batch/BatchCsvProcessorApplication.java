package com.example.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchCsvProcessorApplication {

    public static void main(String[] args) {
        System.exit(
            SpringApplication.exit(
                SpringApplication.run(BatchCsvProcessorApplication.class, args)
            )
        );
    }
}
