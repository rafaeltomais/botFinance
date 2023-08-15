package com.rafael.financebot.scheduledtasks;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EnviarMensagem extends TelegramLongPollingBot {

    private String botUsername = "MyPersonalFinanceJavaBot";
    private String botToken = "6379478593:AAH7KpVzCjRUjoQVbHTmPKjW5tY3oDaAmZw";

    @Override
    public void onUpdateReceived(Update update) {}

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
