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
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID quizId;

    private String quizName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Course course;

    @OneToMany(mappedBy = "quiz",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<Question> questions;

    @OneToMany(mappedBy = "quiz",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<QuizUser> quizUsers;

    public Quiz(UUID quizId, String quizName, Course course) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.course = course;
    }
}