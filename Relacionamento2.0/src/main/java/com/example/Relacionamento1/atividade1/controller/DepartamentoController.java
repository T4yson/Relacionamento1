package com.example.Relacionamento1.atividade1.controller;


import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoDetalheResponse;
import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoRequest;
import com.example.Relacionamento1.atividade1.dto.departamento.DepartamentoResponse;
import com.example.Relacionamento1.atividade1.service.DepartamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atividade1/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponse criarDepartamento(@Valid @RequestBody DepartamentoRequest request) {
        return departamentoService.criarDepartamento(request);
    }

    @GetMapping
    public List<DepartamentoResponse> listar() {
        return departamentoService.listar();
    }


    @GetMapping("/{id}")
    public DepartamentoDetalheResponse buscarPorId(@PathVariable Long id) {
        return departamentoService.buscarPorId(id);
    }
}