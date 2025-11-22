package br.com.fiap.mentalance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do listener RabbitMQ para não falhar na inicialização.
 * Permite que a aplicação inicie mesmo se houver problemas de conexão.
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQListenerConfig {

    /**
     * Configura o container factory do listener para não falhar na inicialização.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.rabbitmq.host")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        // Não falhar na inicialização se houver erro de conexão
        factory.setErrorHandler(new ConditionalRejectingErrorHandler(new FatalExceptionStrategy() {
            @Override
            public boolean isFatal(Throwable t) {
                // Não tratar erros de autenticação como fatais na inicialização
                if (t.getCause() instanceof com.rabbitmq.client.AuthenticationFailureException) {
                    log.warn("Erro de autenticação RabbitMQ (não fatal): {}", t.getMessage());
                    return false;
                }
                return true;
            }
        }));
        
        // Auto-start configurável via propriedade
        factory.setAutoStartup(false);
        
        return factory;
    }
}

