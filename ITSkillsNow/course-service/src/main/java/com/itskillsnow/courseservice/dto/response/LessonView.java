package com.itskillsnow.courseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LessonView {
    private UUID lessonId;
    private String lessonName;
    private String lessonContent;
}
