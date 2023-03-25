package com.itskillsnow.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;


    public User(String fullName, String username, String email, String password) {
        this.fullName=fullName;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
