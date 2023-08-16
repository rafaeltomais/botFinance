package com.rafael.financebot.api.controller;

import com.rafael.financebot.domain.exception.ConflitoStatus;
import com.rafael.financebot.domain.exception.EntidadeNaoEncontrada;
import com.rafael.financebot.domain.exception.PersistenciaDados;
import com.rafael.financebot.domain.exception.ValorInvalido;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.model.Usuario;
import com.rafael.financebot.domain.repository.ContaRepository;
import com.rafael.financebot.domain.repository.UsuarioRepository;
import com.rafael.financebot.domain.service.ContaService;
import com.rafael.financebot.domain.service.UsuarioService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios/{userId}/contas")
public class ContasUsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    ContaService contaService;

    @GetMapping
    public ResponseEntity<?> listarContasUsuario(@PathVariable Long userId,
                                                 @RequestParam(required = false) String status) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

        if(usuarioOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        List<Conta> contasMensais = usuarioOptional.get().getContasMensais();
        contasMensais.sort(Comparator.comparingInt(Conta::getDueDay));

        for (Conta cadaConta : contasMensais) {
            contaService.cadastrarConta(cadaConta);
        }

        if(null == status) {
            return ResponseEntity
                    .ok(contasMensais);
        }

        switch (status) {
            case "open" -> {
                List<Conta> contasAberto = contasMensais.stream()
                        .filter(conta -> !conta.isPayed())
                        .toList();
                return ResponseEntity
                        .ok(contasAberto);
            }
            case "overdue" -> {
                List<Conta> contasVencidas = contasMensais.stream()
                        .filter(Conta::isOverdue)
                        .toList();
                return ResponseEntity
                        .ok(contasVencidas);
            }
            case "payed" -> {
                List<Conta> contasPagas = contasMensais.stream()
                        .filter(Conta::isPayed)
                        .toList();

                return ResponseEntity
                        .ok(contasPagas);
            }
            default -> {
                return ResponseEntity
                        .badRequest()
                        .body("Parâmetro incorreto, parâmetros aceitos: open/overdue/payed.");
            }
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrarConta(@PathVariable Long userId,
                                            @RequestBody Conta conta) {
        try {
            Conta contaCriada = usuarioService.cadastrarContaService(userId, conta);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(contaCriada);
        }catch (PersistenciaDados | ValorInvalido e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }catch (EntidadeNaoEncontrada e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }catch (ConflitoStatus e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{dueId}")
    public ResponseEntity<?> alterarConta(@PathVariable Long userId,
                                          @PathVariable Long dueId,
                                          @RequestBody Conta conta) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        Optional<Conta> contaOptional = contaRepository.findById(dueId);

        if(usuarioOptional.isEmpty() || contaOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Conta contaEncontrada = contaOptional.get();
        BeanUtils.copyProperties(conta, contaEncontrada, "id" );

        try{
            Conta contaNova = usuarioService.cadastrarContaService(usuarioOptional.get().getId(), contaEncontrada);

            return ResponseEntity
                    .ok(contaNova);
        }catch (EntidadeNaoEncontrada e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }catch (ConflitoStatus e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{dueId}")
    public ResponseEntity<?> deletarConta(@PathVariable Long userId,
                                          @PathVariable Long dueId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        Optional<Conta> contaOptional = contaRepository.findById(dueId);

        if(usuarioOptional.isEmpty() || contaOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        boolean existeConta = usuarioOptional.get().getContasMensais().contains(contaOptional.get());

        if(!existeConta) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .build();
        }

        usuarioOptional.get().getContasMensais().remove(contaOptional.get());

        try {
            usuarioService.cadastrarContato(usuarioOptional.get());
            contaService.removerConta(dueId);

            return ResponseEntity
                    .noContent()
                    .build();
        }catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

}
