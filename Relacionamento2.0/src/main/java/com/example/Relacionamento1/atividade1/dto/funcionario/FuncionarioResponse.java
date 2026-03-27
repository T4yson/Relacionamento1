package com.example.Relacionamento1.atividade1.dto.funcionario;

public record FuncionarioResponse (
        Long id,
        String nome,
        String cargo,
        Long departamentoId,
        String departamentoNome
){
}
