package com.rafael.financebot.api.controller;

import com.rafael.financebot.domain.exception.ConflitoStatus;
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

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> listarInfoUsuario(@PathVariable Long userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

        if(usuarioOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok(usuarioOptional.get());
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

    @PutMapping("/{userId}")
    public ResponseEntity<?> alterarUsuario(@PathVariable Long userId,
                                            @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

        if(usuarioOptional.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Usuario usuarioEncontrado = usuarioOptional.get();
        BeanUtils.copyProperties(usuario, usuarioEncontrado, "id");

        try {
            Usuario usuarioNovo = usuarioService.cadastrarContato(usuarioEncontrado);

            return ResponseEntity.ok(usuarioNovo);
        }catch (PersistenciaDados e) {
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
            cadaConta.setPayed(false);

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