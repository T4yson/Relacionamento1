package com.example.Relacionamento1.atividade1.service;

import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioRequest;
import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioResponse;
import com.example.Relacionamento1.atividade1.model.Departamento;
import com.example.Relacionamento1.atividade1.model.Funcionario;
import com.example.Relacionamento1.atividade1.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final DepartamentoService departamentoService;

    public FuncionarioResponse criarFuncionario(FuncionarioRequest request) {
        Departamento departamento = departamentoService.findEntityById(request.departamentoId());
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(request.nome());
        funcionario.setCargo(request.cargo());
        funcionario.setDepartamento(departamento);
        return toResponse(funcionarioRepository.save(funcionario));
    }

    public List<FuncionarioResponse> listar(Long departamentoId, String nome, Long id) {
        if (id != null && nome != null && !nome.isBlank()) {
            return funcionarioRepository.findByIdAndNomeContainingIgnoreCase(id, nome)
                    .map(this::toResponse)
                    .stream()
                    .toList();
        }
        if (departamentoId != null) {
            return funcionarioRepository.findByDepartamentoId(departamentoId).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (nome != null && !nome.isBlank()) {
            return funcionarioRepository.findByNomeContainingIgnoreCase(nome).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return funcionarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FuncionarioResponse buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Funcionario não encontrado: " + id));
    }

    private FuncionarioResponse toResponse(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo(),
                funcionario.getDepartamento().getId(),
                funcionario.getDepartamento().getNome()
        );
    }
}
