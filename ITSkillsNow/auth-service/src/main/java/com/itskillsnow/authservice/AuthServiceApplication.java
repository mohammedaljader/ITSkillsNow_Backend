package com.itskillsnow.authservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Auth API", version = "1.0", description = "Documentation Auth API v1.0"))
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }


//    @Bean
//    CommandLineRunner commandLineRunner(AuthServiceImpl authService){
//        return args -> {
//            authService.saveUser(new User("admin", "admin", "admin@itskillsnow.com", "aass321456"));
//        };
//    }


}
