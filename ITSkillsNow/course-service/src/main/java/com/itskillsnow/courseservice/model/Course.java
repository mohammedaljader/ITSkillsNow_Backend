package com.itskillsnow.courseservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID courseId;

    private String courseName;

    @Column(length=60000)
    private String courseDescription;

    private String courseImage;

    private Double coursePrice;

    private String courseType;

    private String courseLanguage;

    private Boolean courseIsPublished = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @OneToMany(mappedBy = "course",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JsonIgnore
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JsonIgnore
    private List<FavoriteCourse> favorites;

    @OneToMany(mappedBy = "course",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "course",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<Quiz> quizzes;


    public Course(UUID courseId, String courseName, String courseDescription,
                  String courseImage, Double coursePrice, String courseType,
                  String courseLanguage, Boolean courseIsPublished, User user) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseImage = courseImage;
        this.coursePrice = coursePrice;
        this.courseType = courseType;
        this.courseLanguage = courseLanguage;
        this.courseIsPublished = courseIsPublished;
        this.user = user;
    }
}
