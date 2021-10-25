package com.bancolombia.clientsdinner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@RequiredArgsConstructor
@Getter
@Setter
@Data
@Entity(name = "client")
public class Client {

    @Id
    private Integer id;

    private String code;

    private Integer male;

    private Integer type;

    private Integer location;

    private Integer company;

    private Integer encrypt;

    @JsonIgnore
    @OneToMany(mappedBy = "client",fetch = FetchType.EAGER)
    private List<Account> accountList;

    public Client(String code) {
        this.code = code;
    }
}
