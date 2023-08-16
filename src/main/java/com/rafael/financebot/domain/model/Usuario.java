package com.rafael.financebot.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Usuario {

    @EqualsAndHashCode.Include
    @Id
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String userName;

    @Column
    private String password;

    @Column
    private boolean shouldNotificate = false;

    @Column
    private ZonedDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private List<Conta> contasMensais;

    public Usuario() {
        ZoneId brazilZone = ZoneId.of("America/Sao_Paulo");
        this.createdAt = ZonedDateTime.now(brazilZone);
    }

}
