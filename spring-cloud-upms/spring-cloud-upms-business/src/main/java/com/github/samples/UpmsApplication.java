package com.github.samples;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ChengWei.Fu
 * @date 2022/12/12
 */
@SpringBootApplication
@MapperScan("com.github.samples.mapper")
public class UpmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UpmsApplication.class, args);
    }
}
