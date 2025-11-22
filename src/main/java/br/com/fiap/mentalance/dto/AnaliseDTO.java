package br.com.fiap.mentalance.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AnaliseDTO {
    Long checkinId;
    LocalDateTime criadoEm;
    String resumo;
    String recomendacoes;
    String sentimentos;
}

