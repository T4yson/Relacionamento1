package com.example.Relacionamento1.atividade1.repository;

import com.example.Relacionamento1.atividade1.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario,Long> {

    List<Funcionario> findByDepartamentoId(Long departamentoId);

    List<Funcionario> findByNomeContainingIgnoreCase(String nome);

    Optional<Funcionario> findByIdAndNomeContainingIgnoreCase(Long id, String nome);
}
