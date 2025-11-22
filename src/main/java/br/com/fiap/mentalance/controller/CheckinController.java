package br.com.fiap.mentalance.controller;

import br.com.fiap.mentalance.dto.CheckinRequest;
import br.com.fiap.mentalance.model.EstadoHumor;
import br.com.fiap.mentalance.model.Usuario;
import br.com.fiap.mentalance.service.CheckinService;
import br.com.fiap.mentalance.service.SessaoUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class CheckinController {

    private final SessaoUsuarioService sessaoUsuarioService;
    private final CheckinService checkinService;

    @GetMapping("/checkins")
    public String lista(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Usuario usuario = sessaoUsuarioService.getUsuarioAtual();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("data").descending());
        Page<br.com.fiap.mentalance.model.Checkin> checkinsPage = checkinService.listarTodosPaginados(usuario, pageable);
        
        model.addAttribute("checkins", checkinsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", checkinsPage.getTotalPages());
        model.addAttribute("totalItems", checkinsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        return "checkin";
    }

    @GetMapping("/checkins/novo")
    public String form(Model model) {
        model.addAttribute("checkin", new CheckinRequest());
        model.addAttribute("humores", EstadoHumor.values());
        return "checkin-form";
    }

    @PostMapping("/checkins")
    public String salvar(@Valid @ModelAttribute("checkin") CheckinRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("humores", EstadoHumor.values());
            return "checkin-form";
        }

        Usuario usuario = sessaoUsuarioService.getUsuarioAtual();
        checkinService.registrarCheckin(usuario, request, locale);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Check-in registrado com sucesso!");
        return "redirect:/dashboard";
    }
}

