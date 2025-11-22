package br.com.fiap.mentalance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "analises")
public class Analise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 2000)
    private String resumo;

    @Column(length = 2000)
    private String recomendacoes;

    @Column(length = 500)
    private String sentimentos;

    @Column(length = 120)
    private String modelo;

    @Column(length = 8, nullable = false)
    private String idioma = "pt-BR";

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToOne(optional = false)
    @JoinColumn(name = "checkin_id")
    private Checkin checkin;
}

