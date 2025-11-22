package br.com.fiap.mentalance.dto;

import br.com.fiap.mentalance.model.EstadoHumor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckinRequest {

    @NotNull
    private EstadoHumor humor;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer energia;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer sono;

    @NotBlank
    private String contexto;
}

