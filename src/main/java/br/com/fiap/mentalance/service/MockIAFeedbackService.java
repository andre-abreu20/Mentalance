package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.EstadoHumor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Locale;

@Service
@ConditionalOnProperty(value = "openai.enabled", havingValue = "false", matchIfMissing = true)
public class MockIAFeedbackService implements IAFeedbackService {

    private static final Map<EstadoHumor, String> RESUMOS_PADRAO = Map.of(
            EstadoHumor.MUITO_BEM, "Você demonstrou um alto nível de bem-estar hoje. Continue reforçando seus hábitos positivos.",
            EstadoHumor.BEM, "Seu humor está equilibrado. Aproveite para celebrar pequenas vitórias do dia.",
            EstadoHumor.NEUTRO, "Um dia neutro é normal. Que tal reservar um tempo para algo prazeroso?",
            EstadoHumor.CANSADO, "Seu corpo pede descanso. Procure desacelerar e priorizar o sono reparador.",
            EstadoHumor.ESTRESSADO, "Níveis altos de estresse identificados. Respirações profundas e pausas podem ajudar."
    );

    private static final Map<EstadoHumor, String> RESUMOS_EN = Map.of(
            EstadoHumor.MUITO_BEM, "You are showing a high level of wellbeing today. Keep reinforcing what works for you.",
            EstadoHumor.BEM, "Your mood is balanced. Take a moment to celebrate small wins.",
            EstadoHumor.NEUTRO, "Neutral days are normal. Try scheduling something enjoyable for yourself.",
            EstadoHumor.CANSADO, "Your body asks for rest. Slow down and prioritize quality sleep.",
            EstadoHumor.ESTRESSADO, "High stress was detected. Deep breaths and short breaks can make a difference."
    );

    @Override
    public Analise gerarAnalise(Checkin checkin, Locale locale) {
        boolean english = locale != null && locale.getLanguage().startsWith("en");

        Analise analise = new Analise();
        analise.setCheckin(checkin);
        analise.setModelo("mock-local");
        analise.setIdioma(locale != null ? locale.toLanguageTag() : "pt-BR");
        analise.setResumo(criarResumo(checkin, english));
        analise.setRecomendacoes(criarRecomendacoes(checkin, english));
        analise.setSentimentos(checkin.getHumor().name());
        return analise;
    }

    private String criarResumo(Checkin checkin, boolean english) {
        Map<EstadoHumor, String> mapa = english ? RESUMOS_EN : RESUMOS_PADRAO;
        return mapa.getOrDefault(checkin.getHumor(),
                english
                        ? "Check-in saved. Keep tracking your emotional evolution."
                        : "Check-in registrado. Continue acompanhando sua evolução emocional.");
    }

    private String criarRecomendacoes(Checkin checkin, boolean english) {
        String contexto = checkin.getContexto();
        if (english) {
            return switch (checkin.getHumor()) {
                case MUITO_BEM, BEM -> "Keep doing what has worked for you. Write down the habits that boosted you today: " + contexto;
                case NEUTRO -> "Plan a small pleasant activity and see how it feels. Context: " + contexto;
                case CANSADO -> "Reschedule tasks to prioritize rest. Hydration and short breaks can help. Context: " + contexto;
                case ESTRESSADO -> "Take 5 minutes to breathe slowly and share with someone you trust. Context: " + contexto;
            };
        }
        return switch (checkin.getHumor()) {
            case MUITO_BEM, BEM -> "Mantenha as práticas que têm feito bem a você. Considere registrar o que funcionou: " + contexto;
            case NEUTRO -> "Busque pequenas ações que tragam satisfação. Experimente algo novo e leve: " + contexto;
            case CANSADO -> "Reorganize sua agenda para priorizar descanso. Hidratação e breves pausas podem ajudar. Contexto: " + contexto;
            case ESTRESSADO -> "Que tal separar 5 minutos para respirar profundamente? Compartilhe com alguém de confiança. Contexto: " + contexto;
        };
    }
}

