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
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;

    private String questionName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "question",
            cascade= {CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}
    )
    @JsonIgnore
    private List<Option> options;
}
