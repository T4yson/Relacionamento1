package com.example.Relacionamento1.atividade1.dto.funcionario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FuncionarioRequest (
        @NotBlank(message = "Nome do funcionario é obrigatorio")
        String nome,
        @NotBlank(message = "Cargo é obrigatorio")
        String cargo,
        @NotNull(message = "Departamento é obrigatorio")
        Long departamentoId
){
}
