package com.itskillsnow.jobservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String username;

    @OneToMany(mappedBy = "user",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<Job> jobs;


    @OneToMany(mappedBy = "user",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<JobApplication> jobApplications;


    @OneToMany(mappedBy = "user",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<FavoriteJob> myJobs;


    public User(String username) {
        this.username = username;
    }
}
