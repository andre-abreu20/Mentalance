package br.com.fiap.mentalance.controller;

import br.com.fiap.mentalance.dto.DashboardResumoDTO;
import br.com.fiap.mentalance.model.Usuario;
import br.com.fiap.mentalance.service.CheckinService;
import br.com.fiap.mentalance.service.SessaoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final SessaoUsuarioService sessaoUsuarioService;
    private final CheckinService checkinService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Usuario usuario = sessaoUsuarioService.getUsuarioAtual();
        DashboardResumoDTO resumo = checkinService.gerarResumoSemanal(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("resumo", resumo);
        model.addAttribute("checkinsRecentes", checkinService.listarRecentes(usuario));
        model.addAttribute("insightsIa", checkinService.listarAnalises(usuario));

        return "dashboard";
    }
}

