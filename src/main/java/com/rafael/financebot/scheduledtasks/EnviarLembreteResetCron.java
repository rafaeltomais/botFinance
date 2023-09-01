package com.rafael.financebot.scheduledtasks;

import com.rafael.financebot.api.controller.UsuarioController;
import com.rafael.financebot.domain.model.Conta;
import com.rafael.financebot.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnviarLembreteResetCron {

    @Autowired
    private EnviarMensagem enviarMensagem;

    @Autowired
    private UsuarioController usuarioController;

    @Scheduled(cron = "0 30 11 1 * *")
    public void LembreteResetMensagem() {
        List<Usuario> usuarioList = usuarioController.listarUsuarios();

        for(Usuario cadaUsuario : usuarioList) {
            Long identificador = cadaUsuario.getId();
            String chatId = Long.toString(identificador);

            List<Conta> contaList = cadaUsuario.getContasMensais();

            StringBuilder mensagemInicioMes = new StringBuilder();

            if(contaList.size() == 0) {
                mensagemInicioMes.append("Bom dia, champs! 😉\n\nAproveite que o mês está começando e cadastre seus compromissos financeiros para não pagar nenhum juros! Te ajudo com avisos, mas o dinheiro é com você! 🤡");
            }
            else {
                mensagemInicioMes.append("Bom dia, parça! 😉\n\nAgora que o mês está começando, não esqueça de resetar o status de todos os seus compromissos financeiros para eu te enviar notificação em cada vencimento! Assim você não paga nenhum juros! \n\n*No menu inicial escolha a opção 'Configurações' e clique em 'Resetar todas as contas'.");
            }

            enviarMensagem.sendMessage(chatId, String.valueOf(mensagemInicioMes));
        }
    }

}
