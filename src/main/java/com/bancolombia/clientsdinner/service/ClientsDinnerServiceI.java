package com.bancolombia.clientsdinner.service;

import com.bancolombia.clientsdinner.entity.Table;
import com.bancolombia.clientsdinner.entity.TableGroup;

import java.util.List;

public interface ClientsDinnerServiceI {

    List<Integer> findAllClients();

    List<Table> readInputTables();

    List<TableGroup> makeTableFilters();
}
