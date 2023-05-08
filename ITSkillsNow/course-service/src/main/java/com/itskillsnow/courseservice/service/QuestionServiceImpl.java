package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.exception.QuestionNotFoundException;
import com.itskillsnow.courseservice.exception.QuizNotFoundException;
import com.itskillsnow.courseservice.model.Option;
import com.itskillsnow.courseservice.model.Question;
import com.itskillsnow.courseservice.model.Quiz;
import com.itskillsnow.courseservice.repository.OptionRepository;
import com.itskillsnow.courseservice.repository.QuestionRepository;
import com.itskillsnow.courseservice.repository.QuizRepository;
import com.itskillsnow.courseservice.service.interfaces.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionRepository questionRepository;

    private final OptionRepository optionRepository;

    private final QuizRepository quizRepository;

    @Override
    public boolean addQuestion(AddQuestionDto addQuestionDto, List<AddOptionDto> optionDtoList) {
        Optional<Quiz> quiz = quizRepository.findById(addQuestionDto.getQuizId());
        if(quiz.isEmpty()){
            return false;
        }
        Question question = mapQuestionDtoToModel(addQuestionDto, quiz.get());
        Question savedQuestion = questionRepository.save(question);

        List<Option> options = optionDtoList.stream()
                .map(x -> mapOptionDtoToModel(x, savedQuestion)).toList();

        optionRepository.saveAll(options);
        return true;
    }

    @Override
    public boolean addQuestion(AddQuestionDto addQuestionDto) {
        Optional<Quiz> quiz = quizRepository.findById(addQuestionDto.getQuizId());
        if(quiz.isEmpty()){
            return false;
        }
        Question question = mapQuestionDtoToModel(addQuestionDto, quiz.get());
        questionRepository.save(question);
        return true;
    }

    @Override
    public boolean updateQuestion(UpdateQuestionDto updateQuestionDto) {
        Optional<Question> question = questionRepository.findById(updateQuestionDto.getQuestionId());
        if(question.isEmpty()){
            return false;
        }
        Question updatedQuestion = mapQuestionDtoToModel(updateQuestionDto);
        questionRepository.save(updatedQuestion);
        return true;
    }

    @Override
    public boolean deleteQuestion(UUID questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if(question.isEmpty()){
            return false;
        }
        questionRepository.delete(question.get());
        return true;
    }

    @Override
    public boolean addOption(AddOptionDto addOptionDto) {
        Optional<Question> question = questionRepository.findById(addOptionDto.getQuestionId());
        if(question.isEmpty()){
            return false;
        }
        Option option = mapOptionDtoToModel(addOptionDto, question.get());
        optionRepository.save(option);
        return true;
    }

    @Override
    public boolean updateOption(UpdateOptionDto updateOptionDto) {
        Optional<Option> option = optionRepository.findById(updateOptionDto.getOptionId());
        if(option.isEmpty()){
            return false;
        }
        Option updatedOption = mapOptionDtoToModel(updateOptionDto);
        optionRepository.save(updatedOption);
        return true;
    }

    @Override
    public boolean deleteOption(UUID optionId) {
        Optional<Option> option = optionRepository.findById(optionId);
        if(option.isEmpty()){
            return false;
        }
        optionRepository.delete(option.get());
        return true;
    }

    @Override
    public List<QuestionView> getAllQuestionByQuiz(UUID quizId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);
        if(quiz.isEmpty()){
            throw new QuizNotFoundException("Quiz was not found!");
        }
        return questionRepository.findByQuiz(quiz.get())
                .stream()
                .map(this::mapQuestionModelToDto)
                .toList();
    }

    @Override
    public List<OptionView> getAllOptionsByQuestion(UUID questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if(question.isEmpty()){
            throw new QuestionNotFoundException("Question was not found!");
        }
        return optionRepository.findByQuestion(question.get())
                .stream()
                .map(this::mapOptionModelToDto)
                .toList();
    }

    private Question mapQuestionDtoToModel(AddQuestionDto addQuestionDto, Quiz quiz){
        return Question.builder()
                .questionName(addQuestionDto.getQuestionName())
                .quiz(quiz)
                .build();
    }

    private Option mapOptionDtoToModel(AddOptionDto addOptionDto, Question question){
        return Option.builder()
                .optionName(addOptionDto.getOptionName())
                .optionIsCorrect(addOptionDto.isOptionIsCorrect())
                .question(question)
                .build();
    }

    private Question mapQuestionDtoToModel(UpdateQuestionDto updateQuestionDto){
        return Question.builder()
                .questionId(updateQuestionDto.getQuestionId())
                .questionName(updateQuestionDto.getQuestionName())
                .build();
    }

    private Option mapOptionDtoToModel(UpdateOptionDto updateOptionDto){
        return Option.builder()
                .optionId(updateOptionDto.getOptionId())
                .optionName(updateOptionDto.getOptionName())
                .optionIsCorrect(updateOptionDto.isOptionIsCorrect())
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