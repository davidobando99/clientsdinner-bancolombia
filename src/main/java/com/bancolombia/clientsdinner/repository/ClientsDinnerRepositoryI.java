package com.bancolombia.clientsdinner.repository;

import com.bancolombia.clientsdinner.entity.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsDinnerRepositoryI extends CrudRepository<Client, Integer> {

    List<Client> findAll();
}
