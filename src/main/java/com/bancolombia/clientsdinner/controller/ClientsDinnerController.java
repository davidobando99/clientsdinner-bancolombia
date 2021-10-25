package com.bancolombia.clientsdinner.controller;

import com.bancolombia.clientsdinner.entity.Client;
import com.bancolombia.clientsdinner.entity.TableGroup;
import com.bancolombia.clientsdinner.service.ClientsDinnerServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientsDinnerController {

    @Autowired
    private ClientsDinnerServiceI clientDinnerService;

    @GetMapping("/")
    public List<TableGroup> findAllClients(){
        return clientDinnerService.makeTableFilters();
    }
}
