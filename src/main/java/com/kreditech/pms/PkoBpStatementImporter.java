package com.kreditech.pms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PkoBpStatementImporter {

    private static final Logger logger = LoggerFactory.getLogger(PkoBpStatementImporter.class);

    public static void main(String[] args) {
        SpringApplication.run(PkoBpStatementImporter.class, args);
    }


}
