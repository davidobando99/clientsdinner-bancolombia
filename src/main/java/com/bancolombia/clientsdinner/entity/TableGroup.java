package com.bancolombia.clientsdinner.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TableGroup {

    private String tableName;

    private List<String> clientsCodes;


}
