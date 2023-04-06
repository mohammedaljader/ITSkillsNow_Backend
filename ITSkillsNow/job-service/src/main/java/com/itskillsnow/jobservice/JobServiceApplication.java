package com.itskillsnow.jobservice;

import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class JobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
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
