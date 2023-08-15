package com.rafael.financebot.scheduledtasks;

import com.rafael.financebot.api.controller.UsuarioController;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class VerificaVencimentoCron {

    @Autowired
    private EnviarMensagem messageSender;

    @Autowired
    UsuarioController usuarioController;

    @Scheduled(cron = "0 0 12 * * *")
    public void verificaVencimento() {
        LocalDate dataAtual = LocalDate.now();
        int diaDoMes = dataAtual.getDayOfMonth();

        List<Usuario> usuarioList = usuarioController.listarUsuarios();

        for(Usuario cadaUsuario : usuarioList) {
            Long identificador = cadaUsuario.getId();
            String chatId = Long.toString(identificador);
            List<Conta> contaList = cadaUsuario.getContasMensais();

            if(cadaUsuario.isShouldNotificate()) {
                StringBuilder mensagemVencimento = new StringBuilder("🚨 Um alerta para você de vencimento! 🚨\n\n");
                int quantidadeContaAberto = 0;

                for (Conta cadaConta : contaList) {
                    int diaVencimento = cadaConta.getDueDay();
                    String descricaoConta = cadaConta.getDescription();
                    boolean contaEmAberto = !cadaConta.isPayed();
                    boolean isDiaVencimento = (diaDoMes) == diaVencimento;
                    boolean isAmanhaVencimento = (diaDoMes + 1) == diaVencimento;

                    if ((isDiaVencimento || isAmanhaVencimento) && contaEmAberto) {
                        quantidadeContaAberto += 1;
                        mensagemVencimento.append(String.format("Conta '%s' vence dia %d\n", descricaoConta, diaVencimento));
                    }
                }

                if(quantidadeContaAberto > 0){
                    messageSender.sendMessage(chatId, String.valueOf(mensagemVencimento));
                }
            }
        }
    }
}
