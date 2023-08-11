package com.rafael.financebot.api.controller;

import com.rafael.financebot.domain.exception.ConflitoStatus;
import com.rafael.financebot.domain.exception.EntidadeNaoEncontrada;
import com.rafael.financebot.domain.exception.PersistenciaDados;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaService contaService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvarUsuario(@RequestBody Usuario usuario) {
        try{
            usuarioService.cadastrarContato(usuario);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(usuario);
        }catch (PersistenciaDados e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
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
                LocalDate dataAtual = LocalDate.now();
                int diaDoMes = dataAtual.getDayOfMonth();

                List<Conta> contasVencidas = contasMensais.stream()
                        .filter(conta -> conta.getDueDay() < diaDoMes && !conta.isPayed())
                        .toList();
                return ResponseEntity
                        .ok(contasVencidas);
            }
            default -> {
                return ResponseEntity
                        .badRequest()
                        .body("Parâmetro incorreto, parâmetros aceitos: OPEN/OVERDUE.");
            }
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> cadastrarConta(@PathVariable Long userId,
                                            @RequestBody Conta conta) {
        try {
            Conta contaCriada = usuarioService.cadastrarContaService(userId, conta);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(contaCriada);
        }catch (PersistenciaDados e) {
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

    @PutMapping("/{userId}/{dueId}")
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

        BeanUtils.copyProperties(conta, contaOptional.get(), "id" );

        try{
            Conta contaNova = usuarioService.cadastrarContaService(usuarioOptional.get().getId(), contaOptional.get());

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

    @DeleteMapping("/{userId}/{dueId}")
    public ResponseEntity<?> deletar(@PathVariable Long userId,
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

    @PutMapping("/{userId}/reset")
    public ResponseEntity<?> resetarTodosStatus(@PathVariable Long userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

        if(usuarioOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        List<Conta> contaList = usuarioOptional.get().getContasMensais();

        for(Conta cadaConta : contaList) {
//            cadaConta.setDueOpen(true);
            cadaConta.setPayed(false);
//            cadaConta.setOverdue(false);

            try{
                contaService.cadastrarConta(cadaConta);
            }catch (ConflitoStatus e) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(e.getMessage());
            }
        }

        return ResponseEntity
                .ok()
                .body(usuarioOptional.get());
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<?> deletarTodasContas(@PathVariable Long userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

        if(usuarioOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Usuario usuario = usuarioOptional.get();
        List<Conta> contaList = usuario.getContasMensais();

        if(contaList.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(String.format("Não tem cadastro de contas no usuário de ID %d.", userId));
        }

        for(int i = contaList.size() - 1; i >= 0; i--) {
            Conta cadaConta = contaList.get(i);
            usuario.getContasMensais().remove(i);
            contaService.removerConta(cadaConta.getId());
        }

        return ResponseEntity
                .noContent()
                .build();
    }

}