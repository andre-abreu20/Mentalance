package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "openai.enabled", havingValue = "true")
public class OpenAiIAFeedbackService implements IAFeedbackService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Override
    public Analise gerarAnalise(Checkin checkin, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.forLanguageTag("pt-BR");
        boolean english = effectiveLocale.getLanguage().startsWith("en");

        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(model)
                    .instructions(construirPromptSistema(english))
                    .input(construirPromptUsuario(checkin, english))
                    .maxOutputTokens(350L)
                    .build();

            Response openAiResponse = openAIClient.responses().create(params);
            String textoIa = extrairTexto(openAiResponse);

            Analise analise = new Analise();
            analise.setCheckin(checkin);
            analise.setIdioma(effectiveLocale.toLanguageTag());
            analise.setModelo("openai");
            analise.setSentimentos(checkin.getHumor().name());

            ResultadoInsight resultado = interpretarResposta(textoIa, english);
            analise.setResumo(resultado.resumo());
            analise.setRecomendacoes(resultado.sugestoes());

            return analise;
        } catch (Exception ex) {
            log.error("Falha ao gerar insight com OpenAI", ex);
            return fallbackAnalise(checkin, english, locale);
        }
    }

    private String construirPromptSistema(boolean english) {
        if (english) {
            return """
                    You are an empathetic wellbeing assistant focused on daily check-ins.
                    RESPOND ONLY WITH A JSON OBJECT like:
                    {
                      "summary": "paragraph validating emotions",
                      "suggestions": ["practical suggestion 1", "suggestion 2", "suggestion 3"]
                    }
                    No markdown, bullets or any text outside this JSON.
                    """;
        }
        return """
                Você é um mentor empático especializado em saúde mental no trabalho.
                RESPONDA APENAS COM UM JSON NO FORMATO:
                {
                  "resumo": "parágrafo validando as emoções",
                  "recomendacoes": ["sugestão 1", "sugestão 2", "sugestão 3"]
                }
                Não use markdown, negrito ou texto fora desse JSON.
                """;
    }

    private String construirPromptUsuario(Checkin checkin, boolean english) {
        String templatePt = """
                Dados do check-in:
                - Humor: %s
                - Energia: %d
                - Sono: %d
                - Contexto: "%s"

                Reforce o formato JSON obrigatório e mantenha frases curtas.
                """;

        String templateEn = """
                Check-in data:
                - Mood: %s
                - Energy: %d
                - Sleep: %d
                - Context: "%s"

                Remember the mandatory JSON format and keep sentences concise.
                """;

        return String.format(english ? templateEn : templatePt,
                checkin.getHumor(),
                checkin.getEnergia(),
                checkin.getSono(),
                checkin.getContexto());
    }

    private String extrairTexto(Response response) {
        if (response.output() == null) {
            return "";
        }

        return response.output().stream()
                .map(ResponseOutputItem::message)
                .flatMap(Optional::stream)
                .map(message -> message.content().stream()
                        .map(content -> content.outputText()
                                .map(ResponseOutputText::text)
                                .orElse(""))
                        .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n"));
    }

    private ResultadoInsight interpretarResposta(String texto, boolean english) {
        if (texto == null || texto.isBlank()) {
            return new ResultadoInsight("", "");
        }

        try {
            JsonNode root = objectMapper.readTree(texto);
            String resumo = root.path(english ? "summary" : "resumo").asText("");
            JsonNode arr = root.path(english ? "suggestions" : "recomendacoes");

            List<String> sugestoes = new ArrayList<>();
            if (arr.isArray()) {
                arr.forEach(node -> sugestoes.add(node.asText("")));
            }
            String recomBinado = sugestoes.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining("\n"));

            if (resumo.isBlank() && recomBinado.isBlank()) {
                return new ResultadoInsight(texto.trim(), "");
            }

            return new ResultadoInsight(resumo, recomBinado);
        } catch (JsonProcessingException e) {
            log.warn("Não foi possível interpretar o JSON retornado pela IA. Retornando texto bruto.", e);
            return new ResultadoInsight(texto.trim(), "");
        }
    }

    private Analise fallbackAnalise(Checkin checkin, boolean english, Locale locale) {
        Analise analise = new Analise();
        analise.setCheckin(checkin);
        analise.setModelo("openai-fallback");
        analise.setIdioma(locale != null ? locale.toLanguageTag() : "pt-BR");
        analise.setSentimentos(checkin.getHumor().name());
        if (english) {
            analise.setResumo("We couldn't reach the AI service right now, but keep observing your emotions and repeat the check-in later.");
            analise.setRecomendacoes("Take a deep breath, hydrate, and plan a small positive action for the next hour.");
        } else {
            analise.setResumo("Não conseguimos acessar o serviço de IA agora, mas continue observando suas emoções e tente novamente em breve.");
            analise.setRecomendacoes("Respire fundo, hidrate-se e planeje uma pequena ação positiva para a próxima hora.");
        }
        return analise;
    }

    private record ResultadoInsight(String resumo, String sugestoes) {
    }
}

