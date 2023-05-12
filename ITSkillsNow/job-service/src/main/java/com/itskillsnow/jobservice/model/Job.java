package com.itskillsnow.jobservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;

    private String jobName;

    private String jobDescription;

    private String jobImage;

    private String jobAddress;

    private String  jobCategory;

    private String jobEducationLevel;

    private String jobType;

    private Integer jobHours;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @OneToMany(mappedBy = "job",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JsonIgnore
    private List<JobApplication> jobApplications;


    @OneToMany(mappedBy = "job",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST })
    @JsonIgnore
    private List<FavoriteJob>  favorites;

}
