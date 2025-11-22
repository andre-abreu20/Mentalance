package br.com.fiap.mentalance.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ.
 * Define filas e conversores de mensagens.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Nome da fila para mensagens de check-in.
     */
    public static final String QUEUE_CHECKIN = "mentalance.checkin";

    /**
     * Cria a fila de check-ins (durable = true para persistir mensagens).
     */
    @Bean
    public Queue checkinQueue() {
        return new Queue(QUEUE_CHECKIN, true);
    }

    /**
     * Configura o conversor JSON para mensagens.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

