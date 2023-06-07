package com.itskillsnow.authservice.config;

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

    public static final String QUEUE = "auth.queue";

    public static final String MESSAGE_QUEUE = "auth_message_queue";
    public static final String EXCHANGE = "auth.exchange";

    public static final String MESSAGE_EXCHANGE = "auth_message_exchange";
    public static final String ROUTING_KEY = "auth.*";

    public static final String MESSAGE_ROUTING_KEY = "auth_message_routingKey";

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(MESSAGE_EXCHANGE);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(MESSAGE_QUEUE);
    }

    @Bean
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue()).to(authExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding messageBinding() {
        return BindingBuilder.bind(messageQueue()).to(messageExchange()).with(MESSAGE_ROUTING_KEY);
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
