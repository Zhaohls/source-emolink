package com.emolink;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.emolink.mapper")
public class EmoLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmoLinkApplication.class, args);
    }
}

