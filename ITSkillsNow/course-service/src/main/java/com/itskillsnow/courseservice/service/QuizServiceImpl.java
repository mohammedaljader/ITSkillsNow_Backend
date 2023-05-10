package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.quiz.AddQuizDto;
import com.itskillsnow.courseservice.dto.request.quiz.UpdateQuizDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuizView;
import com.itskillsnow.courseservice.dto.response.QuizWithoutQuestionView;
import com.itskillsnow.courseservice.exception.CourseNotFoundException;
import com.itskillsnow.courseservice.exception.QuizNotFoundException;
import com.itskillsnow.courseservice.model.Course;
import com.itskillsnow.courseservice.model.Option;
import com.itskillsnow.courseservice.model.Question;
import com.itskillsnow.courseservice.model.Quiz;
import com.itskillsnow.courseservice.repository.CourseRepository;
import com.itskillsnow.courseservice.repository.QuizRepository;
import com.itskillsnow.courseservice.service.interfaces.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;

    @Override
    public QuizWithoutQuestionView addQuiz(AddQuizDto addQuizDto) {
        Optional<Course> course = courseRepository.findById(addQuizDto.getCourseId());
        if(course.isEmpty()){
            throw new CourseNotFoundException("Course was not found!");
        }
        Quiz quiz = mapAddDtoToModel(addQuizDto, course.get());
        Quiz savedQuiz = quizRepository.save(quiz);
        return mapQuizWithoutQuestionModelToDto(savedQuiz);
    }

    @Override
    public QuizWithoutQuestionView updateQuiz(UpdateQuizDto updateQuizDto) {
        Optional<Quiz> quiz = quizRepository.findById(updateQuizDto.getQuizId());
        if(quiz.isEmpty()){
            throw new QuizNotFoundException("Quiz was not found!");
        }
        Quiz updatedQuiz = mapUpdateDtoToModel(updateQuizDto, quiz.get().getCourse());
        Quiz savedQuiz = quizRepository.save(updatedQuiz);
        return mapQuizWithoutQuestionModelToDto(savedQuiz);
    }

    @Override
    public boolean deleteQuiz(UUID quizId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);
        if(quiz.isEmpty()){
            return false;
        }
        quizRepository.delete(quiz.get());
        return true;
    }

    @Override
    public List<QuizView> getAllQuizzesByCourse(UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new CourseNotFoundException("Course was not found!!");
        }
        List<Quiz> quizzes = quizRepository.findByCourse(course.get());
        return quizzes.stream()
                .map(this::mapModelToDto).toList();
    }

    @Override
    public List<QuizWithoutQuestionView> getAllQuizzesWithoutQuestionsByCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId).
                orElseThrow(() ->  new CourseNotFoundException("Course was not found!!"));
        List<Quiz> quizzes = quizRepository.findByCourse(course);
        return quizzes.stream()
                .map(this::mapQuizWithoutQuestionModelToDto).toList();
    }

    private Quiz mapAddDtoToModel(AddQuizDto addQuizDto, Course course){
        return Quiz.builder()
                .quizName(addQuizDto.getQuizName())
                .course(course)
                .build();
    }

    private Quiz mapUpdateDtoToModel(UpdateQuizDto updateQuizDto, Course course){
        return new Quiz(updateQuizDto.getQuizId(), updateQuizDto.getQuizName(), course);
    }

    private QuizWithoutQuestionView mapQuizWithoutQuestionModelToDto(Quiz quiz){
        return QuizWithoutQuestionView.builder()
                .quizId(quiz.getQuizId())
                .quizName(quiz.getQuizName())
                .build();
    }

    private QuizView mapModelToDto(Quiz quiz){
        List<QuestionView> questions = quiz.getQuestions().stream()
                .map(this::mapQuestionModelToDto).toList();

        return QuizView.builder()
                .quizId(quiz.getQuizId())
                .quizName(quiz.getQuizName())
                .questions(questions)
                .build();
    }

    private QuestionView mapQuestionModelToDto(Question question){
        List<OptionView> options = question.getOptions().stream()
                .map(this::mapOptionModelToDto).toList();

        return QuestionView.builder()
                .questionId(question.getQuestionId())
                .questionName(question.getQuestionName())
                .questionOptions(options)
                .build();
    }

    private OptionView mapOptionModelToDto(Option option){
        return OptionView.builder()
                .optionId(option.getOptionId())
                .optionName(option.getOptionName())
                .optionIsCorrect(option.isOptionIsCorrect())
                .build();
    }
}
