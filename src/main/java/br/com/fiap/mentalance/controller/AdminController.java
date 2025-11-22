package br.com.fiap.mentalance.controller;

import br.com.fiap.mentalance.service.CheckinService;
import br.com.fiap.mentalance.service.SessaoUsuarioService;
import br.com.fiap.mentalance.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final CheckinService checkinService;
    private final SessaoUsuarioService sessaoUsuarioService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("usuario", sessaoUsuarioService.getUsuarioAtual());
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalCheckins", checkinService.contarCheckins());
        model.addAttribute("mediaEnergia", checkinService.mediaEnergiaGlobal());
        model.addAttribute("mediaSono", checkinService.mediaSonoGlobal());
        model.addAttribute("checkinsRecentes", checkinService.listarRecentesGlobal());
        return "admin";
    }
}

