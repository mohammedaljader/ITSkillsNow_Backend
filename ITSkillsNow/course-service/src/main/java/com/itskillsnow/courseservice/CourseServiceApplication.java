package com.itskillsnow.courseservice;

import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Course API", version = "1.0", description = "Documentation Course API v1.0"))
public class CourseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository){
        return args -> {

            User user1 = new User("User1");
            User user2 = new User("User2");

            userRepository.save(user1);
            userRepository.save(user2);
        };
    }
}
