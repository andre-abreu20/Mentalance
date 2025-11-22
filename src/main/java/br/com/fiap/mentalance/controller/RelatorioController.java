package br.com.fiap.mentalance.controller;

import br.com.fiap.mentalance.dto.CheckinGraficoDTO;
import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.Usuario;
import br.com.fiap.mentalance.service.CheckinService;
import br.com.fiap.mentalance.service.SessaoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RelatorioController {

    private final SessaoUsuarioService sessaoUsuarioService;
    private final CheckinService checkinService;

    @GetMapping("/relatorio")
    public String relatorio(
            @RequestParam(name = "inicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {

        Usuario usuario = sessaoUsuarioService.getUsuarioAtual();

        LocalDate fimEfetivo = fim != null ? fim : LocalDate.now();
        LocalDate inicioEfetivo = inicio != null ? inicio : fimEfetivo.minusDays(30);

        List<Checkin> checkins = checkinService.listarPorPeriodo(usuario, inicioEfetivo, fimEfetivo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault());
        List<CheckinGraficoDTO> checkinsGrafico = checkins.stream()
                .map(checkin -> CheckinGraficoDTO.builder()
                        .data(checkin.getData() != null ? checkin.getData().format(formatter) : "")
                        .energia(checkin.getEnergia())
                        .sono(checkin.getSono())
                        .build())
                .toList();

        model.addAttribute("inicio", inicioEfetivo);
        model.addAttribute("fim", fimEfetivo);
        model.addAttribute("checkins", checkins);
        model.addAttribute("checkinsGrafico", checkinsGrafico);
        model.addAttribute("usuario", usuario);
        model.addAttribute("resumo", checkinService.gerarResumoSemanal(usuario));

        return "relatorio";
    }
}

