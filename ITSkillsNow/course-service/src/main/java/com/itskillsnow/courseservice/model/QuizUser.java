package com.itskillsnow.courseservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID quizUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Quiz quiz;


    private Integer score;

    private Integer questionsSize;

    private Integer grade;
}
