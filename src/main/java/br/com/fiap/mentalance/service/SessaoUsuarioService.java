package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.exception.NegocioException;
import br.com.fiap.mentalance.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessaoUsuarioService {

    private final UsuarioService usuarioService;

    public Usuario getUsuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NegocioException("Usuário não autenticado.");
        }

        String username = authentication.getName();
        return usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new NegocioException("Usuário logado não encontrado."));
    }
}

