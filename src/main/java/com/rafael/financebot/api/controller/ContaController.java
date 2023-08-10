package com.rafael.financebot.api.controller;

import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaRepository contaRepository;


    @GetMapping
    public List<Conta> listar() {
        return contaRepository.findAll();
    }

    @GetMapping("{dueId}")
    public ResponseEntity<?> buscaConta(@PathVariable Long dueId) {
        Optional<Conta> contaOptional = contaRepository.findById(dueId);

        if(contaOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity
                .ok(contaOptional.get());

    }

}
