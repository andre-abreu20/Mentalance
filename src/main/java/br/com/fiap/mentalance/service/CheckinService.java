package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.dto.AnaliseDTO;
import br.com.fiap.mentalance.dto.CheckinRequest;
import br.com.fiap.mentalance.dto.DashboardResumoDTO;
import br.com.fiap.mentalance.exception.NegocioException;
import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.EstadoHumor;
import br.com.fiap.mentalance.model.Usuario;
import br.com.fiap.mentalance.repository.AnaliseRepository;
import br.com.fiap.mentalance.repository.CheckinRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final AnaliseRepository analiseRepository;
    private final IAFeedbackService iaFeedbackService;

    @Transactional
    public Checkin registrarCheckin(Usuario usuario, CheckinRequest dto, Locale locale) {
        if (usuario == null) {
            throw new NegocioException("Usuário não encontrado para registro de check-in.");
        }
        Locale idioma = locale != null ? locale : Locale.forLanguageTag("pt-BR");

        Checkin checkin = new Checkin();
        checkin.setUsuario(usuario);
        checkin.setHumor(dto.getHumor());
        checkin.setEnergia(dto.getEnergia());
        checkin.setSono(dto.getSono());
        checkin.setContexto(dto.getContexto());

        Checkin salvo = checkinRepository.save(checkin);

        Analise analise = iaFeedbackService.gerarAnalise(salvo, idioma);
        Analise analiseSalva = analiseRepository.save(analise);
        salvo.setAnalise(analiseSalva);

        return salvo;
    }

    public List<Checkin> listarRecentes(Usuario usuario) {
        return checkinRepository.findTop7ByUsuarioOrderByDataDesc(usuario);
    }

    public List<Checkin> listarTodos(Usuario usuario) {
        return checkinRepository.findAllByUsuarioOrderByDataDesc(usuario);
    }

    public List<AnaliseDTO> listarAnalises(Usuario usuario) {
        return analiseRepository.findTop20ByCheckinUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(analise -> AnaliseDTO.builder()
                        .checkinId(analise.getCheckin().getId())
                        .criadoEm(analise.getCriadoEm())
                        .resumo(analise.getResumo())
                        .recomendacoes(analise.getRecomendacoes())
                        .sentimentos(analise.getSentimentos())
                        .build())
                .toList();
    }

    public DashboardResumoDTO gerarResumoSemanal(Usuario usuario) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        List<Checkin> semana = checkinRepository.buscarPorPeriodo(usuario, inicioSemana, hoje);

        if (semana.isEmpty()) {
            return DashboardResumoDTO.builder()
                    .totalCheckins(0)
                    .mediaEnergia(0)
                    .mediaSono(0)
                    .humorPredominante(null)
                    .distribuicaoHumor(Map.of())
                    .build();
        }

        Map<EstadoHumor, Long> contagemPorHumor = semana.stream()
                .collect(Collectors.groupingBy(Checkin::getHumor, Collectors.counting()));

        EstadoHumor humorPredominante = contagemPorHumor.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        double mediaEnergia = semana.stream()
                .mapToInt(Checkin::getEnergia)
                .average()
                .orElse(0);

        double mediaSono = semana.stream()
                .mapToInt(Checkin::getSono)
                .average()
                .orElse(0);

        Map<String, Long> distribuicaoHumor = contagemPorHumor.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue));

        return DashboardResumoDTO.builder()
                .totalCheckins(semana.size())
                .mediaEnergia(mediaEnergia)
                .mediaSono(mediaSono)
                .humorPredominante(humorPredominante)
                .distribuicaoHumor(distribuicaoHumor)
                .build();
    }

    public List<Checkin> listarPorPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim) {
        return checkinRepository.buscarPorPeriodo(usuario, inicio, fim);
    }

    public long contarCheckins() {
        return checkinRepository.count();
    }

    public double mediaEnergiaGlobal() {
        return checkinRepository.findAll().stream()
                .mapToInt(Checkin::getEnergia)
                .average()
                .orElse(0);
    }

    public double mediaSonoGlobal() {
        return checkinRepository.findAll().stream()
                .mapToInt(Checkin::getSono)
                .average()
                .orElse(0);
    }

    public List<Checkin> listarRecentesGlobal() {
        return checkinRepository.findTop10ByOrderByDataDesc();
    }
}

