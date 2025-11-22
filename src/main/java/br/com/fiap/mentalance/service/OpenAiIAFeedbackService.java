package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
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

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "openai.enabled", havingValue = "true")
public class OpenAiIAFeedbackService implements IAFeedbackService {

    private final OpenAIClient openAIClient;

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

            ResultadoInsight resultado = separarResumoESugestoes(textoIa, english);
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
                    Always answer in English with two sections:
                    1) Summary: one short paragraph validating emotions.
                    2) Suggestions: a bullet list (1-3 items) with practical actions.
                    Do not mention policies or that you are an AI.
                    """;
        }
        return """
                Você é um mentor empático especializado em saúde mental no trabalho.
                Responda sempre em português do Brasil, com duas seções:
                1) Resumo: um parágrafo curto validando as emoções.
                2) Sugestões: lista com 1-3 ações práticas.
                Não cite políticas nem mencione que é um modelo de IA.
                """;
    }

    private String construirPromptUsuario(Checkin checkin, boolean english) {
        String templatePt = """
                Dados do check-in:
                - Humor: %s
                - Energia: %d
                - Sono: %d
                - Contexto: "%s"

                Gere o retorno seguindo o formato solicitado (Resumo + Sugestões).
                """;

        String templateEn = """
                Check-in data:
                - Mood: %s
                - Energy: %d
                - Sleep: %d
                - Context: "%s"

                Follow the requested format (Summary + Suggestions).
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

    private ResultadoInsight separarResumoESugestoes(String texto, boolean english) {
        if (texto == null || texto.isBlank()) {
            return new ResultadoInsight("", "");
        }

        String marcadorResumo = english ? "summary:" : "resumo:";
        String marcadorSugestoes = english ? "suggestions:" : "sugest";

        String lower = texto.toLowerCase();
        int idxResumo = lower.indexOf(marcadorResumo);
        int idxSugestoes = lower.indexOf(marcadorSugestoes);

        if (idxResumo >= 0 && idxSugestoes > idxResumo) {
            String resumo = texto.substring(idxResumo + marcadorResumo.length(), idxSugestoes).trim();
            String sugestoes = texto.substring(idxSugestoes + marcadorSugestoes.length()).trim();
            return new ResultadoInsight(resumo, sugestoes);
        }

        return new ResultadoInsight(texto.trim(), "");
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

