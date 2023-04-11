package com.itskillsnow.userservice.config;


import lombok.AllArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfig {

    private final ConnectionFactory connectionFactory;

    public static final String AUTH_QUEUE = "auth_user.queue";

    public static final String COURSE_USER_QUEUE = "course_user.queue";

    public static final String JOB_USER_QUEUE = "job_user.queue";
    public static final String EXCHANGE = "user.exchange";
    public static final String ROUTING_KEY = "user.*";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue authUserQueue() {
        return new Queue(AUTH_QUEUE);
    }

    @Bean
    public Queue courseUserQueue() {
        return new Queue(COURSE_USER_QUEUE);
    }

    @Bean
    public Queue jobUserQueue() {
        return new Queue(JOB_USER_QUEUE);
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(authUserQueue()).to(userExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding CourseUserBinding() {
        return BindingBuilder.bind(courseUserQueue()).to(userExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding JobUserBinding() {
        return BindingBuilder.bind(jobUserQueue()).to(userExchange()).with(ROUTING_KEY);
    }


    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonConverter());
        return factory;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
