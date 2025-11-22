package br.com.fiap.mentalance.dto;

import br.com.fiap.mentalance.model.EstadoHumor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para mensagens enviadas ao RabbitMQ quando um check-in é registrado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long checkinId;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private EstadoHumor humor;
    private Integer energia;
    private Integer sono;
    private String contexto;
    private LocalDate data;
    private LocalDateTime criadoEm;
    private Boolean analiseGerada;
    private String modeloAnalise;
}

