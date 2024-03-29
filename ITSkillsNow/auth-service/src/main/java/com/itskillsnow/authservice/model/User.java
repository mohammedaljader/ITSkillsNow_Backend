package com.itskillsnow.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false,unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ElementCollection(targetClass = Role.class)
    private List<Role> roles;


    public User(String fullName, String username, String email, String password) {
        this.fullName=fullName;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
