package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.course.AddCourseDto;
import com.itskillsnow.courseservice.dto.request.course.AddCourseWithFileDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseDto;
import com.itskillsnow.courseservice.dto.request.course.UpdateCourseWithFileDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.GeneralException;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.BlobService;
import com.itskillsnow.courseservice.service.interfaces.CourseService;
import com.itskillsnow.courseservice.util.FileNamingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final BlobService blobService;

    @Override
    public CourseView addCourse(AddCourseDto courseDto) {
        Optional<User> user = userRepository.findByUsername(courseDto.getUsername());
        if(user.isEmpty()){
            throw new UserNotFoundException("User was not found!");
        }
        Course course = mapCreateDtoToModel(courseDto, user.get());
        Course savedCourse = courseRepository.save(course);
        return mapModelToDto(savedCourse);
    }

    @Override
    public CourseView addCourse(AddCourseWithFileDto courseDto) throws IOException {
        Optional<User> user = userRepository.findByUsername(courseDto.getUsername());
        if(user.isEmpty() || courseDto.getCourseImage().isEmpty()){
            throw new GeneralException("User or course was not found!");
        }

        String courseImage = blobService.storeFile(courseDto.getCourseImage().getOriginalFilename(),
                    courseDto.getCourseImage().getInputStream(),
                    courseDto.getCourseImage().getSize());
        Course newCourse = mapCreateDtoToModel(courseDto, user.get(), courseImage);
        Course savedCourse = courseRepository.save(newCourse);
        return mapModelToDto(savedCourse);
    }

    @Override
    public CourseView updateCourse(UpdateCourseDto courseDto) {
        Course course = courseRepository.findById(courseDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        Course updatedCourse = mapUpdateDtoToModel(course, courseDto);
        Course savedCourse = courseRepository.save(updatedCourse);
        return mapModelToDto(savedCourse);
    }

    @Override
    public CourseView updateCourse(UpdateCourseWithFileDto courseDto) throws IOException {
        Course course = courseRepository.findById(courseDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course was not found!"));

        String courseImage = courseDto.getCourseImage().getOriginalFilename();
        String originalImage = FileNamingUtils.getOriginalFilename(course.getCourseImage());

        if(!Objects.equals(courseDto.getCourseImage().getOriginalFilename(), "") &&
                !Objects.equals(courseImage, originalImage)){
            courseImage = blobService.storeFile(courseDto.getCourseImage().getOriginalFilename(),
                    courseDto.getCourseImage().getInputStream(),
                    courseDto.getCourseImage().getSize());
            //delete the old image from blob storage
            String blobFileName = FileNamingUtils.getBlobFilename(course.getCourseImage());
            blobService.deleteFile(blobFileName);
        }else {
            courseImage = course.getCourseImage();
        }

        Course updatedCourse = mapUpdateDtoToModel(course, courseDto, courseImage);
        Course savedCourse = courseRepository.save(updatedCourse);
        return mapModelToDto(savedCourse);
    }

    @Override
    public Boolean deleteCourse(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            return false;
        }
        //delete the image from blob storage
        String blobFileName = FileNamingUtils.getBlobFilename(course.get().getCourseImage());
        blobService.deleteFile(blobFileName);
        course.ifPresent(courseRepository::delete);
        return true;
    }

    @Override
    public CourseView getCourseById(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new CourseNotFoundException("Course with id: ".concat(courseId.toString()).concat(" not found!"));
        }
        return mapModelToDto(course.get());
    }

    @Override
    public List<CourseView> getAllCoursesByUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("User with username: ".concat(username).concat(" not found!"));
        }
        List<Course> courses = courseRepository.findAllByUser(user.get());
        return courses.stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseView> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapModelToDto)
                .toList();
    }

    private CourseView mapModelToDto(Course course){
        return CourseView.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseDescription(course.getCourseDescription())
                .courseImage(course.getCourseImage())
                .coursePrice(course.getCoursePrice())
                .courseType(course.getCourseType())
                .courseLanguage(course.getCourseLanguage())
                .username(course.getUser().getUsername())
                .build();
    }

    private Course mapCreateDtoToModel(AddCourseDto courseDto, User user){
        return Course.builder()
                .courseName(courseDto.getCourseName())
                .courseDescription(courseDto.getCourseDescription())
                .courseImage(courseDto.getCourseImage())
                .coursePrice(courseDto.getCoursePrice())
                .courseType(courseDto.getCourseType())
                .courseLanguage(courseDto.getCourseLanguage())
                .user(user)
                .build();
    }

    private Course mapCreateDtoToModel(AddCourseWithFileDto courseDto, User user, String courseImage){
        return Course.builder()
                .courseName(courseDto.getCourseName())
                .courseDescription(courseDto.getCourseDescription())
                .courseImage(courseImage)
                .coursePrice(courseDto.getCoursePrice())
                .courseType(courseDto.getCourseType())
                .courseLanguage(courseDto.getCourseLanguage())
                .user(user)
                .build();
    }

    private Course mapUpdateDtoToModel(Course course ,UpdateCourseDto courseDto){
        course.setCourseName(courseDto.getCourseName());
        course.setCourseDescription(courseDto.getCourseDescription());
        course.setCourseImage(courseDto.getCourseImage());
        course.setCoursePrice(courseDto.getCoursePrice());
        course.setCourseType(courseDto.getCourseType());
        course.setCourseLanguage(courseDto.getCourseLanguage());
        course.setCourseIsPublished(courseDto.isPublished());
        return course;
    }

    private Course mapUpdateDtoToModel(Course course, UpdateCourseWithFileDto courseDto, String courseImage){
        course.setCourseName(courseDto.getCourseName());
        course.setCourseDescription(courseDto.getCourseDescription());
        course.setCourseImage(courseImage);
        course.setCoursePrice(courseDto.getCoursePrice());
        course.setCourseType(courseDto.getCourseType());
        course.setCourseLanguage(courseDto.getCourseLanguage());
        course.setCourseIsPublished(courseDto.isPublished());
        return course;

    }
}
