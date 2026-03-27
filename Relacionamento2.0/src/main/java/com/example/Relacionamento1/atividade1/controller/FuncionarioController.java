package com.example.Relacionamento1.atividade1.controller;


import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioRequest;
import com.example.Relacionamento1.atividade1.dto.funcionario.FuncionarioResponse;
import com.example.Relacionamento1.atividade1.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("atividade1/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponse criarFuncionario(@Valid @RequestBody FuncionarioRequest request) {
        return funcionarioService.criarFuncionario(request);
    }

    @GetMapping
    public List<FuncionarioResponse> listar(
        @RequestParam(required = false) Long departametoId,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) Long id
    ) {
            return funcionarioService.listar(departametoId, nome, id);
    }

    @GetMapping("/{id}")
    public FuncionarioResponse buscarPorId(@PathVariable Long id) {return funcionarioService.buscarPorId(id);}
}
