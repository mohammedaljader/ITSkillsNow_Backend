package com.itskillsnow.courseservice.unitTests;


import com.itskillsnow.courseservice.dto.request.favorites.AddCourseToFavoritesDto;
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
import com.itskillsnow.courseservice.service.FavoriteCourseServiceImpl;
import com.itskillsnow.courseservice.service.interfaces.FavoriteCourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FavoriteCourseServiceImplTest {

    private static final String userNotFound = "User was not found!";

    private static final String courseNotFound = "Course was not found!";

    private static final String favoriteNotFound = "Favorite course was not found!";

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FavoriteCourseRepository favoriteCourseRepository;

    @Mock
    private UserRepository userRepository;

    private FavoriteCourseService favoriteCourseService;

    private static final String username = "User";

    @BeforeEach
    void setUp() {
        favoriteCourseService = new FavoriteCourseServiceImpl(favoriteCourseRepository, userRepository, courseRepository);
    }


    @Test
    void givenCourseToFavorites_withCorrectData_returnsTrue(){
        // Arrange
        User user = new User(username);
        Course course = getCourse(user);
        AddCourseToFavoritesDto addCourseToFavoritesDto = new AddCourseToFavoritesDto(course.getCourseId(), username);
        boolean expected = true;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findById(course.getCourseId())).thenReturn(Optional.of(course));
        when(favoriteCourseRepository.existsByCourse(course)).thenReturn(false);

        // Act
        boolean actual = favoriteCourseService.addCourseToFavorites(addCourseToFavoritesDto);

        // Assert
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findById(course.getCourseId());
        verify(favoriteCourseRepository, times(1)).save(any(FavoriteCourse.class));
        verify(favoriteCourseRepository, times(1)).existsByCourse(course);

    }

    @Test
    void givenCourseToFavorites_courseAlreadyExistsInFavoriteList_returnsFalse(){
        // Arrange
        User user = new User(username);
        Course course = getCourse(user);
        AddCourseToFavoritesDto addCourseToFavoritesDto = new AddCourseToFavoritesDto(course.getCourseId(), username);
        boolean expected = false;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findById(course.getCourseId())).thenReturn(Optional.of(course));
        when(favoriteCourseRepository.existsByCourse(course)).thenReturn(true);

        // Act
        boolean actual = favoriteCourseService.addCourseToFavorites(addCourseToFavoritesDto);

        // Assert
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findById(course.getCourseId());
        verify(favoriteCourseRepository, times(0)).save(any(FavoriteCourse.class));
        verify(favoriteCourseRepository, times(1)).existsByCourse(course);

    }


    @Test
    void givenCourseToFavorites_withWrongUsername_returnsFalse(){
        // Arrange
        Course course = getCourse(new User("WrongUsername"));
        AddCourseToFavoritesDto addCourseToFavoritesDto = new AddCourseToFavoritesDto(course.getCourseId(), "WrongUsername");

        when(userRepository.findByUsername("WrongUsername")).thenReturn(Optional.empty());

        // Act
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                favoriteCourseService.addCourseToFavorites(addCourseToFavoritesDto)
        );

        // Assert
        assertEquals(userNotFound, actual.getMessage());

        verify(userRepository, times(1)).findByUsername("WrongUsername");
        verify(courseRepository, times(0)).findById(course.getCourseId());
        verify(favoriteCourseRepository, times(0)).save(any(FavoriteCourse.class));
        verify(favoriteCourseRepository, times(0)).existsByCourse(any(Course.class));
    }


    @Test
    void givenCourseToFavorites_withWrongCourseId_returnsFalse(){
        // Arrange
        User user = new User(username);
        Course course = getCourse(user);
        AddCourseToFavoritesDto addCourseToFavoritesDto = new AddCourseToFavoritesDto(course.getCourseId(), username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(courseRepository.findById(course.getCourseId())).thenReturn(Optional.empty());

        // Act
        CourseNotFoundException actual = Assertions.assertThrows(CourseNotFoundException.class, () ->
                favoriteCourseService.addCourseToFavorites(addCourseToFavoritesDto)
        );

        // Assert
        assertEquals(courseNotFound, actual.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(courseRepository, times(1)).findById(course.getCourseId());
        verify(favoriteCourseRepository, times(0)).save(any(FavoriteCourse.class));
        verify(favoriteCourseRepository, times(0)).existsByCourse(any(Course.class));
    }

    @Test
    void givenDeleteCourseToFavorites_withCorrectFavoriteCourseId_returnsTrue(){
        // Arrange

        UUID favoriteId = UUID.randomUUID();
        User user = new User(username);
        FavoriteCourse favoriteCourse = getFavoriteCourse(favoriteId, user);
        boolean expected = true;

        when(favoriteCourseRepository.findById(favoriteId)).thenReturn(Optional.of(favoriteCourse));

        // Act
        boolean actual = favoriteCourseService.deleteCourseFromFavorites(favoriteId);

        // Assert
        assertEquals(expected, actual);

        verify(favoriteCourseRepository, times(1)).findById(favoriteId);
        verify(favoriteCourseRepository, times(1)).delete(any(FavoriteCourse.class));
    }


    @Test
    void givenDeleteCourseToFavorites_withWrongFavoriteCourseId_returnsTrue(){
        // Arrange
        UUID favoriteId = UUID.randomUUID();

        when(favoriteCourseRepository.findById(favoriteId)).thenReturn(Optional.empty());

        // Act
        FavoriteCourseNotFound actual = Assertions.assertThrows(FavoriteCourseNotFound.class, () ->
                favoriteCourseService.deleteCourseFromFavorites(favoriteId)
        );

        // Assert
        assertEquals(favoriteNotFound, actual.getMessage());

        verify(favoriteCourseRepository, times(1)).findById(favoriteId);
        verify(favoriteCourseRepository, times(0)).delete(any(FavoriteCourse.class));
    }

    @Test
    void givenGetAllFavoritesByUser_withCorrectUsername_returnsAllFavorites(){
        // Arrange
        UUID favoriteId1 = UUID.randomUUID();
        UUID favoriteId2 = UUID.randomUUID();
        UUID favoriteId3 = UUID.randomUUID();
        User user = new User(username);

        FavoriteCourse favoriteCourse1 = getFavoriteCourse(favoriteId1, user);
        FavoriteCourse favoriteCourse2 = getFavoriteCourse(favoriteId2, user);
        FavoriteCourse favoriteCourse3 = getFavoriteCourse(favoriteId3, user);


        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(favoriteCourseRepository.findAllByUser(user)).thenReturn(List.of(favoriteCourse1, favoriteCourse2, favoriteCourse3));


        // Act
        List<FavoriteCourseView> actual = favoriteCourseService.getAllFavorites(username);

        // Assert
        assertEquals(3, actual.size());

        verify(userRepository, times(1)).findByUsername(username);
        verify(favoriteCourseRepository, times(1)).findAllByUser(user);
    }


    @Test
    void givenGetAllFavoritesByUser_withWrongUsername_returnsException(){
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        String expected = "User was not found!";

        // Act
        UserNotFoundException actual = Assertions.assertThrows(UserNotFoundException.class, () ->
                favoriteCourseService.getAllFavorites(username)
        );


        // Assert
        assertEquals(expected, actual.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(favoriteCourseRepository, times(0)).findAllByUser(any(User.class));
    }






    private Course getCourse(User user){
        UUID courseId = UUID.randomUUID();
        String courseName = "CourseName";
        String courseDescription = "CourseDescription";
        String courseImage = "CourseImage";
        Double coursePrice = 10.0;
        String courseType = "CourseType";
        String courseLanguage = "C#";

        return Course.builder()
                .courseId(courseId)
                .courseName(courseName)
                .courseDescription(courseDescription)
                .courseImage(courseImage)
                .coursePrice(coursePrice)
                .courseType(courseType)
                .courseLanguage(courseLanguage)
                .user(user)
                .build();
    }

    private FavoriteCourse getFavoriteCourse(UUID favoriteId,User user){
        Course course = new Course();
        course.setUser(user);
        return FavoriteCourse.builder()
                .favoriteId(favoriteId)
                .user(user)
                .course(course)
                .build();
    }
}
