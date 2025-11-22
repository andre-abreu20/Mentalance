package br.com.fiap.mentalance.dto;

import br.com.fiap.mentalance.model.EstadoHumor;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class DashboardResumoDTO {
    long totalCheckins;
    EstadoHumor humorPredominante;
    double mediaEnergia;
    double mediaSono;
    Map<String, Long> distribuicaoHumor;
}

