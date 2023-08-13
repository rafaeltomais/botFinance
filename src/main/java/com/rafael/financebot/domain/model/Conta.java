package com.rafael.financebot.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Conta {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int dueDay;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal dueValue;

    @Column(nullable = false)
    private boolean payed;

    @Column(nullable = false)
    private boolean overdue;

}
