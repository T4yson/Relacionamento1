package com.example.Relacionamento1.atividade1.service;

import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoDetalheResponse;
import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoRequest;
import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoResponse;
import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioResumoResponse;
import com.example.Relacionamento1.atividade1.model.Departamento;
import com.example.Relacionamento1.atividade1.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoResponse criarDepartamento(DepartamentoRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNome(request.nome());
        return toResponse(departamentoRepository.save(departamento));
    }

    public List<DepartamentoResponse> listar() {
        return departamentoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DepartamentoDetalheResponse buscarPorId(Long id) {
        Departamento departamento = findEntityById(id);
        List<FuncionarioResumoResponse> funcionarios = departamento.getFuncionarios().stream()
                .map(funcionario -> new FuncionarioResumoResponse(
                        funcionario.getId(),
                        funcionario.getNome(),
                        funcionario.getCargo()
                ))
                .toList();
        return new DepartamentoDetalheResponse(departamento.getId(), departamento.getNome(), funcionarios);
    }

    public Departamento findEntityById(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado: " + id));
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(departamento.getId(), departamento.getNome());
    }
}