package br.com.fiap.mentalance.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Configuração do OpenAI.
 * Só é ativada se openai.enabled=true E OPENAI_API_KEY estiver definida e não vazia.
 * Se a API key não estiver configurada, o MockIAFeedbackService será usado automaticamente.
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "openai.enabled", havingValue = "true")
public class OpenAIConfig {

    @Bean
    public OpenAIClient openAIClient() {
        // Verifica se a API key está configurada
        String apiKey = Optional.ofNullable(System.getenv("OPENAI_API_KEY"))
                .orElse(Optional.ofNullable(System.getProperty("OPENAI_API_KEY")).orElse(""));
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("OPENAI_API_KEY não está definida. OpenAI será desabilitado. Usando MockIAFeedbackService.");
            throw new IllegalStateException("OPENAI_API_KEY não está configurada. Configure a variável de ambiente OPENAI_API_KEY no Azure ou defina OPENAI_ENABLED=false para usar o serviço mock.");
        }
        
        log.info("OPENAI_API_KEY encontrada (tamanho: {}). Configurando OpenAI Client.", apiKey.length());
        
        // O OpenAIOkHttpClient.fromEnv() lê a variável OPENAI_API_KEY automaticamente
        // Se não conseguir ler, lançará uma exceção que impedirá a criação do bean
        try {
            OpenAIClient client = OpenAIOkHttpClient.fromEnv();
            log.info("OpenAI Client criado com sucesso");
            return client;
        } catch (Exception e) {
            log.error("Erro ao criar OpenAI Client: {}", e.getMessage(), e);
            throw new IllegalStateException("Erro ao configurar OpenAI Client. Verifique se OPENAI_API_KEY está correta e acessível. Erro: " + e.getMessage(), e);
        }
    }
}

