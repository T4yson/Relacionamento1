package com.example.Relacionamento1.atividade1.dto.departamento;

import jakarta.validation.constraints.NotBlank;

public record DepartamentoRequest (
        @NotBlank(message = "Nome do departamento é obrigatorio")
        String nome
) {
}
