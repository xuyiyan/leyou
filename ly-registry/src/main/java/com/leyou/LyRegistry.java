package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author lsd
 * @Date 2020/4/27 21:21
 **/
@EnableEurekaServer //开启eureka server
@SpringBootApplication
public class LyRegistry {

    public static void main(String[] args) {

        SpringApplication.run(LyRegistry.class,args);

    }


}
