package com.rafael.financebot.domain.service;

import com.rafael.financebot.domain.exception.EntidadeNaoEncontrada;
import com.rafael.financebot.domain.exception.PersistenciaDados;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.model.Usuario;
import com.rafael.financebot.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaService contaService;

    public Usuario cadastrarContato(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        }catch (DataIntegrityViolationException e) {
            throw new PersistenciaDados("Erro no corpo da requisição.");
        }
    }

    public Conta cadastrarContaService(Long userId, Conta conta) {
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);

            if(usuarioOptional.isEmpty()) {
                throw new EntidadeNaoEncontrada(String.format("Usuario com ID %d não encontrado.", userId));
            }

            Usuario usuario = usuarioOptional.get();
            Conta contaCriada = contaService.cadastrarConta(conta);

            usuario.getContasMensais().add(contaCriada);
            cadastrarContato(usuario);

            return contaCriada;
        }catch (DataIntegrityViolationException e) {
            throw new PersistenciaDados("Erro de dados no corpo da requisição.");
        }
    }

}
