package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.dto.UsuarioRegistroRequest;
import br.com.fiap.mentalance.model.PerfilUsuario;
import br.com.fiap.mentalance.model.Usuario;
import br.com.fiap.mentalance.repository.UsuarioRepository;
import br.com.fiap.mentalance.exception.NegocioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(UsuarioRegistroRequest request, boolean administrador) {
        validarRegistro(request);

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setUsername(request.getUsername());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(administrador ? PerfilUsuario.ADMIN : PerfilUsuario.USUARIO);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    public java.util.List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    private void validarRegistro(UsuarioRegistroRequest request) {
        if (!request.getSenha().equals(request.getConfirmarSenha())) {
            throw new NegocioException("As senhas não conferem");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new NegocioException("E-mail já cadastrado");
        }

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new NegocioException("Nome de usuário já cadastrado");
        }
    }
}

