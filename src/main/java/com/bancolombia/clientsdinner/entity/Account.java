package com.bancolombia.clientsdinner.entity;

import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
@Getter
@Setter
@Data
@Entity(name = "account")
public class Account {
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    private double balance;

}
