package com.rafael.financebot.domain.service;

import com.rafael.financebot.domain.exception.ConflitoStatus;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Conta cadastrarConta(Conta conta){
        if(conta.isPayed() && conta.isDueOpen()) {
            throw new ConflitoStatus("Uma conta não pode estar paga e aberta ao mesmo tempo.");
        }
        return contaRepository.save(conta);
    }

    public void removerConta(Long dueId) {
        contaRepository.deleteById(dueId);
    }

}
