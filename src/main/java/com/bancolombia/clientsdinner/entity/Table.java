package com.bancolombia.clientsdinner.entity;

import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
@Data
public class Table {

    private String tableName;

    private Optional<Integer> clientType;

    private Optional<Integer> geoCode;

    private Optional<Integer> initialBalance;

    private Optional<Integer> finalBalance;



}
