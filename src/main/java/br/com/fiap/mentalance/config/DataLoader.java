package br.com.fiap.mentalance.config;

import br.com.fiap.mentalance.dto.UsuarioRegistroRequest;
import br.com.fiap.mentalance.repository.UsuarioRepository;
import br.com.fiap.mentalance.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            UsuarioRegistroRequest admin = new UsuarioRegistroRequest();
            admin.setNome("Administrador");
            admin.setEmail("admin@mentalance.com");
            admin.setUsername("admin");
            admin.setSenha("admin123");
            admin.setConfirmarSenha("admin123");

            usuarioService.registrarUsuario(admin, true);
        }
    }
}

