package com.rafael.financebot.domain.service;

import com.rafael.financebot.domain.exception.ValorInvalido;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Conta cadastrarConta(Conta conta){
        if(conta.getDueDay() > 31 || conta.getDueDay() < 1) {
            throw new ValorInvalido("Dia de vencimento deve ser de 1 a 31.");
        }

        ZoneId brazilZone = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime brazilDateTime = ZonedDateTime.now(brazilZone);
        int diaDoMes = brazilDateTime.getDayOfMonth();

        conta.setOverdue((diaDoMes > conta.getDueDay()) && !conta.isPayed());

        return contaRepository.save(conta);
    }

    public void removerConta(Long dueId) {
        contaRepository.deleteById(dueId);
    }

}
