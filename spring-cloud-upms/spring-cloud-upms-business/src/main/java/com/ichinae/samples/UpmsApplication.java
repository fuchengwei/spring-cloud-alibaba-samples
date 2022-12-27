package com.ichinae.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author fuchengwei
 * @date 2020/12/18 9:48 上午
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan("com.ichinae.samples.mapper")
public class UpmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UpmsApplication.class, args);
    }
}
