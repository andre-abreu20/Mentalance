package br.com.fiap.mentalance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String landingPage(Model model) {
        model.addAttribute("useContainer", Boolean.FALSE);
        return "home";
    }
}

