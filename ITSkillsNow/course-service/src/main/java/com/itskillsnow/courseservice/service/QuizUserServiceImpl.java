package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.quizResult.UserAnswersDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuizResultView;
import com.itskillsnow.courseservice.dto.response.QuizView;
import com.itskillsnow.courseservice.exception.OptionNotFoundException;
import com.itskillsnow.courseservice.exception.QuizNotFoundException;
import com.itskillsnow.courseservice.exception.UserNotFoundException;
import com.itskillsnow.courseservice.model.*;
import com.itskillsnow.courseservice.repository.QuizRepository;
import com.itskillsnow.courseservice.repository.QuizUserRepository;
import com.itskillsnow.courseservice.repository.UserRepository;
import com.itskillsnow.courseservice.service.interfaces.QuizUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizUserServiceImpl implements QuizUserService {

    private final QuizRepository quizRepository;

    private final UserRepository userRepository;

    private final QuizUserRepository quizUserRepository;


    @Override
    public Boolean submitQuiz(UUID quizId, String username, List<UserAnswersDto> submitQuizzes) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz was not found!"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        Integer quizResult = checkQuizResult(quiz, submitQuizzes);

        QuizUser quizUser = QuizUser.builder()
                .quiz(quiz)
                .user(user)
                .score(quizResult)
                .build();

        quizUserRepository.save(quizUser);
        return true;
    }

    @Override
    public QuizResultView getQuizResultByUser(UUID quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz was not found!"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        QuizUser quizUser = quizUserRepository.findByQuizAndUser(quiz, user);
        return mapResultsToDto(quizUser);
    }

    @Override
    public List<QuizResultView> getAllQuizzesResultByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        List<QuizUser> quizzesResult = quizUserRepository.findAllByUser(user);
        return quizzesResult.stream()
                .map(this::mapResultsToDto)
                .toList();
    }


    private Integer checkQuizResult(Quiz quiz, List<UserAnswersDto> userAnswers) {
        Map<UUID, UUID> userAnswersMap = userAnswers.stream()
                .collect(Collectors.toMap(UserAnswersDto::getQuestionId, UserAnswersDto::getOptionId));

        int score = 0;
        for (Question question : quiz.getQuestions()) {
            UUID selectedOptionId = userAnswersMap.get(question.getQuestionId());
            if (selectedOptionId != null) {
                Option selectedOption = question.getOptions().stream()
                        .filter(option -> option.getOptionId().equals(selectedOptionId))
                        .findFirst().orElseThrow(() -> new OptionNotFoundException("Option not found"));
                if (selectedOption.isOptionIsCorrect()) {
                    score++;
                }
            }
        }
        return score;
    }

    private QuizResultView mapResultsToDto(QuizUser quizUser){
        QuizView quizView = mapQuizModelToDto(quizUser.getQuiz());

        return QuizResultView.builder()
                .quizUserId(quizUser.getQuizUserId())
                .quiz(quizView)
                .username(quizUser.getUser().getUsername())
                .result(quizUser.getScore())
                .build();
    }

    private QuizView mapQuizModelToDto(Quiz quiz){
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
