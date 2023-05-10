package com.itskillsnow.courseservice.repository;

import com.itskillsnow.courseservice.model.Option;
import com.itskillsnow.courseservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByQuestion(Question question);
}
