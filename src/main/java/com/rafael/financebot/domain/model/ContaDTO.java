package com.rafael.financebot.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ContaDTO {

    private Long id;
    private int dueDay;
    private String description;
    private BigDecimal dueValue;
    private boolean isPayed = false;
    private boolean isOpen = true;
    private boolean isOverdue = false;
    private Usuario usuario;

    public ContaDTO(Conta conta) {
        this.id = conta.getId();
        this.dueDay = conta.getDueDay();
        this.description = conta.getDescription();
        this.dueValue = conta.getDueValue();
        this.isOpen = conta.isDueOpen();
        this.isOverdue = conta.isOverdue();
        this.isPayed = conta.isPayed();
//        this.usuario = conta.getUsuario();
    }
}
