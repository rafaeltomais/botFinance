package com.rafael.financebot.scheduledtasks;

import com.rafael.financebot.api.controller.UsuarioController;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class VerificaVencimentoCron {

    @Autowired
    private EnviarMensagem enviarMensagem;

    @Autowired
    UsuarioController usuarioController;

    @Scheduled(cron = "0 0 12 * * *")
    public void verificaVencimento() {
        LocalDate dataAtual = LocalDate.now();
        int diaDoMes = dataAtual.getDayOfMonth();
        DayOfWeek diaSemana = dataAtual.getDayOfWeek();

        List<Usuario> usuarioList = usuarioController.listarUsuarios();

        for(Usuario cadaUsuario : usuarioList) {
            Long identificador = cadaUsuario.getId();
            String chatId = Long.toString(identificador);

            List<Conta> contaList = cadaUsuario.getContasMensais();
            contaList.sort(Comparator.comparingInt(Conta::getDueDay));

            if(cadaUsuario.isShouldNotificate()) {
                StringBuilder mensagemVencimento = new StringBuilder("🚨 Um alerta de vencimento! 🚨\n\n");
                int quantidadeContaAberto = 0;

                for (Conta cadaConta : contaList) {
                    String descricaoConta = cadaConta.getDescription();
                    int diaVencimento = cadaConta.getDueDay();

                    boolean contaEmAberto = !cadaConta.isPayed();
                    boolean contaVencida = cadaConta.isOverdue();

                    boolean isDiaVencimento = (diaDoMes) == diaVencimento;
                    boolean isAmanhaVencimento = (diaDoMes + 1) == diaVencimento;
                    boolean isVencidaOntem = (diaDoMes - 1) == diaVencimento;
                    boolean isVencidaAnteontem = (diaDoMes - 2) == diaVencimento;

                    if(contaEmAberto) {
                        if(isVencidaAnteontem && diaSemana == DayOfWeek.MONDAY) {
                            quantidadeContaAberto += 1;
                            mensagemVencimento.append(String.format("Conta '%s' vencida sábado.\n", descricaoConta));
                        }
                        if(isVencidaOntem && contaVencida) {
                            quantidadeContaAberto += 1;
                            mensagemVencimento.append(String.format("Conta '%s' vencida ontem.\n", descricaoConta));
                        }
                        if(isDiaVencimento) {
                            quantidadeContaAberto += 1;
                            mensagemVencimento.append(String.format("Conta '%s' vence hoje.\n", descricaoConta));
                        }
                        if(isAmanhaVencimento) {
                            quantidadeContaAberto += 1;
                            mensagemVencimento.append(String.format("Conta '%s' vence amanhã.\n", descricaoConta));
                        }
                    }
                }

                if(quantidadeContaAberto > 0){
                    enviarMensagem.sendMessage(chatId, String.valueOf(mensagemVencimento));
                }
            }
        }
    }
}
