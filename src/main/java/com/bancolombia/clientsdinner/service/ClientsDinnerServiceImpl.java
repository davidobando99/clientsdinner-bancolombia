package com.bancolombia.clientsdinner.service;

import com.bancolombia.clientsdinner.delegate.DecryptClient;
import com.bancolombia.clientsdinner.entity.Account;
import com.bancolombia.clientsdinner.entity.Client;
import com.bancolombia.clientsdinner.entity.Table;
import com.bancolombia.clientsdinner.entity.TableGroup;
import com.bancolombia.clientsdinner.repository.ClientsDinnerRepositoryI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClientsDinnerServiceImpl implements ClientsDinnerServiceI {

    @Autowired
    private ClientsDinnerRepositoryI clientDinnerRepo;

    @Autowired
    private DecryptClient decryptClient;

    private final Function<Client, Double> sumAmount = (client) -> client.getAccountList().stream().
            mapToDouble(Account::getBalance).sum();

    public List<Integer> findAllClients() {
        return clientDinnerRepo.findAll().stream().map(client -> client.getAccountList().size()).collect(Collectors.toList());
    }

    public List<Table> readInputTables() {
        List<Table> tables = new ArrayList<>();
        try (InputStream in = Thread.currentThread().
                getContextClassLoader().
                getResourceAsStream("entrada.txt")) {
            assert in != null;
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<String> convertedTableList = Stream.of(text.split("\n"))
                    .map(String::trim)
                    .collect(Collectors.toList());
            List<String> resultantTableList = new ArrayList<>(convertedTableList);
            convertedTableList = convertedTableList.stream().filter(s -> s.contains("<")).collect(Collectors.toList());


            while (!resultantTableList.isEmpty()) {
                List<String> tempTableList = new ArrayList<>();
                resultantTableList = resultantTableList.stream().dropWhile(s -> s.contains("<")).collect(Collectors.toList());
                tempTableList.add(convertedTableList.get(0));
                tempTableList.addAll(resultantTableList.stream().takeWhile(s -> !s.contains("<")).collect(Collectors.toList()));
                resultantTableList = resultantTableList.stream().dropWhile(s -> !s.contains("<")).collect(Collectors.toList());
                convertedTableList.remove(0);
                tables.add(convertListToTable(tempTableList));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tables;
    }

    public Table convertListToTable(List<String> tableInfo) {
        String tableName = "";
        Optional<Integer> clientType = Optional.empty();
        Optional<Integer> geoCode = Optional.empty();
        Optional<Integer> initialBalance = Optional.empty();
        Optional<Integer> finalBalance = Optional.empty();

        for (String info : tableInfo) {
            Optional<String> infoValue = Optional.of(info.substring(info.lastIndexOf(":") + 1));
            if (info.contains("<")) tableName = info;
            else if (info.contains("TC")) clientType = infoValue.map(Integer::valueOf);
            else if (info.contains("UG")) geoCode = infoValue.map(Integer::valueOf);
            else if (info.contains("RI")) initialBalance = infoValue.map(Integer::valueOf);
            else if (info.contains("RF")) finalBalance = infoValue.map(Integer::valueOf);
        }
        return new Table(tableName, clientType, geoCode, initialBalance, finalBalance);

    }

    public List<TableGroup> makeTableFilters() {
        List<Table> tables = readInputTables();
        List<Client> clients = clientDinnerRepo.findAll();

        return tables.stream().map(table -> {
            List<Client> clientsFilter = new ArrayList<>(clients);
            TableGroup tableResult = new TableGroup();
            if (table.getClientType().isPresent()) {
                clientsFilter = clientsFilter.stream().filter(c -> Objects.equals(c.getType(), table.getClientType().get())).collect(Collectors.toList());
            }
            if (table.getGeoCode().isPresent()) {
                clientsFilter = clientsFilter.stream().filter(c -> Objects.equals(c.getLocation(), table.getGeoCode().get())).collect(Collectors.toList());
            }
            clientsFilter = filterClientsByAccountBalance(clientsFilter, table.getInitialBalance(), table.getFinalBalance());
            clientsFilter = makeGenericFilters(clientsFilter);
            tableResult.setTableName(table.getTableName());
            tableResult.setClientsCodes(clientsFilter.stream().map(Client::getCode).collect(Collectors.toList()));
            return tableResult;
        }).collect(Collectors.toList());
    }

    public List<Client> filterClientsByAccountBalance(List<Client> clients, Optional<Integer> RI, Optional<Integer> RF) {

        List<Client> filteredClients = clients.stream().filter(client ->
                        RI.isPresent() && RF.isPresent() ? (sumAmount.apply(client) >= RI.get() && sumAmount.apply(client) <= RF.get()) :
                                RI.map(value -> sumAmount.apply(client) >= value).
                                        orElseGet(() -> RF.map(value -> sumAmount.apply(client) <= value).orElse(true)))
                .collect(Collectors.toList());

        return filteredClients;
    }

    public List<Client> makeGenericFilters(List<Client> clients) {
        List<Client> result = new ArrayList<>();
        clients = clients.stream().peek(client->{
            if (client.getEncrypt() == 1)
                client.setCode(decryptClient.decryptClientCode(client.getCode()).replace("\"", ""));
        }).collect(Collectors.toList());
        if (clients.size() > 4) {
            Map<Integer, Client> oneByCompanyWithMaxAmount = clients.stream()
                    .collect(Collectors.toMap(
                            Client::getCompany,      // Group By Company
                            Function.identity(),  // Keep clients
                            BinaryOperator.maxBy( // when two or more people with same company exists, keep the client with max balance amount
                                    Comparator.comparingDouble(sumAmount::apply))));
            clients = new ArrayList<>(oneByCompanyWithMaxAmount.values());

            clients.sort((client1, client2) -> {
                int isComp = sumAmount.apply(client2).compareTo(sumAmount.apply(client1));
                return isComp == 0 ? client1.getCode().compareTo(client2.getCode()) : isComp;
            });

            List<Client> women = clients.stream().filter(c -> c.getMale()==0).collect(Collectors.toList());
            List<Client> men = clients.stream().filter(c -> c.getMale()==1).collect(Collectors.toList());


            if(clients.size()>8){
                result.addAll(women.subList(0,3));
                result.addAll(men.subList(0,3));
                result.sort((client1, client2) -> {
                    int isComp = sumAmount.apply(client2).compareTo(sumAmount.apply(client1));
                    return isComp == 0 ? client1.getCode().compareTo(client2.getCode()) : isComp;
                });

                return result;
            }

        }else{
            result.add(new Client("CANCELADA"));
            return result;
        }
        return clients;
    }
}
