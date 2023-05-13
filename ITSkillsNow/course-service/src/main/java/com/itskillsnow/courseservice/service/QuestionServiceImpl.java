package com.itskillsnow.courseservice.service;

import com.itskillsnow.courseservice.dto.request.option.AddOptionDto;
import com.itskillsnow.courseservice.dto.request.option.UpdateOptionDto;
import com.itskillsnow.courseservice.dto.request.question.AddQuestionDto;
import com.itskillsnow.courseservice.dto.request.question.UpdateQuestionDto;
import com.itskillsnow.courseservice.dto.response.OptionView;
import com.itskillsnow.courseservice.dto.response.QuestionView;
import com.itskillsnow.courseservice.dto.response.QuestionWithoutOptionView;
import com.itskillsnow.courseservice.exception.OptionNotFoundException;
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
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionRepository questionRepository;

    private final OptionRepository optionRepository;

    private final QuizRepository quizRepository;

    private static final String quizNotFound = "Quiz was not found!";

    private static final String questionNotFound = "Question was not found!";

    private static final String optionNotFound = "Option was not found!";


    @Override
    public boolean addQuestion(AddQuestionDto addQuestionDto, List<AddOptionDto> optionDtoList) {
        Quiz quiz = quizRepository.findById(addQuestionDto.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException(quizNotFound));

        Question question = mapUpdatedQuestionDtoToModel(addQuestionDto, quiz);
        Question savedQuestion = questionRepository.save(question);

        List<Option> options = optionDtoList.stream()
                .map(x -> mapOptionDtoToModel(x, savedQuestion)).toList();

        optionRepository.saveAll(options);
        return true;
    }

    @Override
    public QuestionWithoutOptionView addQuestion(AddQuestionDto addQuestionDto) {
        Quiz quiz = quizRepository.findById(addQuestionDto.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException(quizNotFound));

        Question question = mapUpdatedQuestionDtoToModel(addQuestionDto, quiz);
        Question savedQuestion = questionRepository.save(question);
        return mapQuestionModelWithoutOptionToDto(savedQuestion);
    }

    @Override
    public QuestionWithoutOptionView updateQuestion(UpdateQuestionDto updateQuestionDto) {
        Question question = questionRepository.findById(updateQuestionDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(questionNotFound));

        Question updatedQuestion = mapUpdatedQuestionDtoToModel(question, updateQuestionDto);
        Question savedQuestion = questionRepository.save(updatedQuestion);
        return mapQuestionModelWithoutOptionToDto(savedQuestion);
    }

    @Override
    public boolean deleteQuestion(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionNotFound));

        questionRepository.delete(question);
        return true;
    }

    @Override
    public OptionView addOption(AddOptionDto addOptionDto) {
        Question question = questionRepository.findById(addOptionDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(questionNotFound));

        Option option = mapOptionDtoToModel(addOptionDto, question);
        Option savedOption = optionRepository.save(option);
        return mapOptionModelToDto(savedOption);
    }

    @Override
    public OptionView updateOption(UpdateOptionDto updateOptionDto) {
        Option option = optionRepository.findById(updateOptionDto.getOptionId())
                .orElseThrow(() -> new OptionNotFoundException(optionNotFound));

        Option updatedOption = mapUpdatedOptionDtoToModel(option, updateOptionDto);
        Option savedOption = optionRepository.save(updatedOption);
        return mapOptionModelToDto(savedOption);
    }

    @Override
    public boolean deleteOption(UUID optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotFoundException(optionNotFound));

        optionRepository.delete(option);
        return true;
    }

    @Override
    public List<QuestionView> getAllQuestionByQuiz(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizNotFound));

        return questionRepository.findByQuiz(quiz)
                .stream()
                .map(this::mapQuestionModelToDto)
                .toList();
    }

    @Override
    public List<OptionView> getAllOptionsByQuestion(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionNotFound));

        return optionRepository.findByQuestion(question)
                .stream()
                .map(this::mapOptionModelToDto)
                .toList();
    }

    private Question mapUpdatedQuestionDtoToModel(AddQuestionDto addQuestionDto, Quiz quiz){
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

    private Question mapUpdatedQuestionDtoToModel(Question question, UpdateQuestionDto updateQuestionDto){
        question.setQuestionName(updateQuestionDto.getQuestionName());
        return question;
    }

    private Option mapUpdatedOptionDtoToModel(Option option, UpdateOptionDto updateOptionDto){
        option.setOptionName(updateOptionDto.getOptionName());
        option.setOptionIsCorrect(updateOptionDto.isOptionIsCorrect());
        return option;
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

    private QuestionWithoutOptionView mapQuestionModelWithoutOptionToDto(Question question){
        return QuestionWithoutOptionView.builder()
                .questionId(question.getQuestionId())
                .questionName(question.getQuestionName())
                .build();
    }
}
