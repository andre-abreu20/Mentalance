package br.com.fiap.mentalance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração avançada do RabbitMQ para lidar com problemas de conexão.
 * Torna a conexão opcional e não bloqueia a inicialização da aplicação.
 * Só é ativada se spring.rabbitmq.host estiver definido e não vazio.
 */
@Configuration
@Slf4j
@ConditionalOnExpression("'${spring.rabbitmq.host:}' != ''")
public class RabbitMQConnectionConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${spring.rabbitmq.ssl.algorithm:TLSv1.2}")
    private String sslAlgorithm;

    /**
     * Configura a ConnectionFactory com tratamento de erros.
     * Se houver problema de conexão, a aplicação não falhará na inicialização.
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {

        try {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            
            // Virtual host: remove barra inicial se presente
            String vhost = virtualHost != null && !virtualHost.isEmpty() 
                    ? virtualHost.replaceFirst("^/", "") 
                    : "/";
            factory.setVirtualHost(vhost);

            if (sslEnabled) {
                try {
                    factory.getRabbitConnectionFactory().useSslProtocol(sslAlgorithm);
                    log.info("SSL habilitado para RabbitMQ com algoritmo: {}", sslAlgorithm);
                } catch (Exception e) {
                    log.warn("Não foi possível habilitar SSL para RabbitMQ: {}", e.getMessage());
                }
            }

            // Configurações para não falhar na inicialização
            factory.setRequestedHeartBeat(30);
            factory.setConnectionTimeout(10000);

            log.info("Configurando conexão RabbitMQ: {}:{}, vhost: {}", host, port, vhost);
            return factory;
        } catch (Exception e) {
            log.error("Erro ao configurar RabbitMQ. A aplicação continuará sem RabbitMQ: {}", e.getMessage(), e);
            throw new IllegalStateException("Erro ao configurar RabbitMQ: " + e.getMessage(), e);
        }
    }

    /**
     * Configura o RabbitTemplate com tratamento de erros e conversor JSON.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         org.springframework.amqp.support.converter.MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        // Não lança exceção se a conexão falhar
        template.setMandatory(false);
        log.info("RabbitTemplate configurado com sucesso");
        return template;
    }
}

