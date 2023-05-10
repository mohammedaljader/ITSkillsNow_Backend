package com.itskillsnow.courseservice.dto.request.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateLessonDto {
    private UUID lessonId;
    private String lessonName;
    private String lessonContent;
}
