package br.com.fiap.mentalance.controller;

import br.com.fiap.mentalance.service.CheckinService;
import br.com.fiap.mentalance.service.SessaoUsuarioService;
import br.com.fiap.mentalance.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final CheckinService checkinService;
    private final SessaoUsuarioService sessaoUsuarioService;

    @GetMapping
    public String dashboard(
            @RequestParam(defaultValue = "0") int pageUsuarios,
            @RequestParam(defaultValue = "0") int pageCheckins,
            @RequestParam(defaultValue = "10") int sizeUsuarios,
            @RequestParam(defaultValue = "10") int sizeCheckins,
            Model model) {
        model.addAttribute("usuario", sessaoUsuarioService.getUsuarioAtual());
        
        // Paginação de usuários
        Pageable pageableUsuarios = PageRequest.of(pageUsuarios, sizeUsuarios, Sort.by("criadoEm").descending());
        Page<br.com.fiap.mentalance.model.Usuario> usuariosPage = usuarioService.listarUsuariosPaginados(pageableUsuarios);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("usuariosCurrentPage", pageUsuarios);
        model.addAttribute("usuariosTotalPages", usuariosPage.getTotalPages());
        model.addAttribute("usuariosTotalItems", usuariosPage.getTotalElements());
        
        // Estatísticas
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalCheckins", checkinService.contarCheckins());
        model.addAttribute("mediaEnergia", checkinService.mediaEnergiaGlobal());
        model.addAttribute("mediaSono", checkinService.mediaSonoGlobal());
        
        // Paginação de check-ins recentes
        Pageable pageableCheckins = PageRequest.of(pageCheckins, sizeCheckins, Sort.by("data").descending());
        Page<br.com.fiap.mentalance.model.Checkin> checkinsPage = checkinService.listarRecentesGlobalPaginados(pageableCheckins);
        model.addAttribute("checkinsRecentes", checkinsPage.getContent());
        model.addAttribute("checkinsCurrentPage", pageCheckins);
        model.addAttribute("checkinsTotalPages", checkinsPage.getTotalPages());
        model.addAttribute("checkinsTotalItems", checkinsPage.getTotalElements());
        
        return "admin";
    }
}

