package com.itskillsnow.courseservice.dto.request.lesson;

import lombok.Data;

import java.util.UUID;

@Data
public class AddLessonDto {
    private String lessonName;
    private String lessonContent;
    private UUID courseId;
}
