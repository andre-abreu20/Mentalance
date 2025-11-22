package br.com.fiap.mentalance.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CheckinGraficoDTO {
    String data;
    Integer energia;
    Integer sono;
}

