package br.com.fiap.mentalance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 120)
    private String senha;

    @NotBlank
    @Size(min = 6, max = 120)
    private String confirmarSenha;
}

