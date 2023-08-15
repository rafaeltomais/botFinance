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

    @Scheduled(cron = "0 0 9 * * *")
    public void verificaVencimento() {
        LocalDate dataAtual = LocalDate.now();
        int diaDoMes = dataAtual.getDayOfMonth();

        List<Usuario> usuarioList = usuarioController.listarUsuarios();
        StringBuilder mensagemVencimento = new StringBuilder("🚨🚨 Um alerta para você de vencimento! 🚨🚨\n\n");

        for(Usuario cadaUsuario : usuarioList) {
            Long identificador = cadaUsuario.getId();
            String chatId = Long.toString(identificador);
            List<Conta> contaList = cadaUsuario.getContasMensais();

            if(cadaUsuario.isShouldNotificate()) {
                for (Conta cadaConta : contaList) {
                    int diaVencimento = cadaConta.getDueDay();
                    String descricaoConta = cadaConta.getDescription();

                    if (diaVencimento == diaDoMes) {

                        mensagemVencimento.append(String.format("Conta %s para o dia %d\n", descricaoConta, diaVencimento));
                    }
                }

                messageSender.sendMessage(chatId, String.valueOf(mensagemVencimento));
            }
        }
    }
}
