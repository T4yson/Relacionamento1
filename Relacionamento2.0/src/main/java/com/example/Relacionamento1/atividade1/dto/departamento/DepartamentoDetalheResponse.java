package com.example.Relacionamento1.atividade1.dto.departamento;

import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioResumoResponse;

import java.util.List;

public record DepartamentoDetalheResponse(
        Long id,
        String nome,
        List<FuncionarioResumoResponse> funcionarios
) {
}
