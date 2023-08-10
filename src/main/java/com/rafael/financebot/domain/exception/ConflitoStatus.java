package com.rafael.financebot.domain.exception;

public class ConflitoStatus extends RuntimeException {
    public ConflitoStatus(String mensagem) {
        super(mensagem);
    }
}
