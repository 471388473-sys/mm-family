package com.pos.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pos.platform.mapper")
public class PosPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosPlatformApplication.class, args);
    }
}