package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.favorites.AddCourseToFavoritesDto;
import com.itskillsnow.courseservice.dto.response.CourseView;
import com.itskillsnow.courseservice.dto.response.FavoriteCourseView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.FavoriteCourseNotFound;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.FavoriteCourse;
import com.itskillsnow.courseservice.model.User;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.FavoriteCourseRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.FavoriteCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteCourseServiceImpl implements FavoriteCourseService {

    private final FavoriteCourseRepository favoriteCourseRepository;

    private final UserRepository userRepository;

    private final CourseRepository courseRepository;

    private static final String userNotFound = "User was not found!";

    private static final String courseNotFound = "Course was not found!";

    private static final String favoriteNotFound = "Favorite course was not found!";

    @Override
    public boolean addCourseToFavorites(AddCourseToFavoritesDto addCourseToFavoritesDto) {
        User user = userRepository.findByUsername(addCourseToFavoritesDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userNotFound));

        Course course = courseRepository.findById(addCourseToFavoritesDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(courseNotFound));

        FavoriteCourse favoriteCourse = mapDtoToModel(user, course);
        favoriteCourseRepository.save(favoriteCourse);
        return true;
    }

    @Override
    public boolean deleteCourseFromFavorites(UUID favoriteId) {
        FavoriteCourse favoriteCourse = favoriteCourseRepository.findById(favoriteId)
                        .orElseThrow(() -> new FavoriteCourseNotFound(favoriteNotFound));
        favoriteCourseRepository.delete(favoriteCourse);
        return true;
    }

    @Override
    public List<FavoriteCourseView> getAllFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(userNotFound));

        return favoriteCourseRepository.findAllByUser(user)
                .stream()
                .map(this::mapModelToDto)
                .collect(Collectors.toList());
    }

    private FavoriteCourse mapDtoToModel(User user, Course course){
        return FavoriteCourse.builder()
                .favoriteDate(LocalDate.now())
                .favoriteTime(LocalTime.now())
                .user(user)
                .course(course)
                .build();
    }

    private FavoriteCourseView mapModelToDto(FavoriteCourse favoriteCourse){
        return FavoriteCourseView.builder()
                .favoriteId(favoriteCourse.getFavoriteId())
                .favoriteDate(favoriteCourse.getFavoriteDate())
                .favoriteTime(favoriteCourse.getFavoriteTime())
                .courseView(mapCourseModelToDto(favoriteCourse.getCourse()))
                .build();
    }

    private CourseView mapCourseModelToDto(Course course){
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
}
