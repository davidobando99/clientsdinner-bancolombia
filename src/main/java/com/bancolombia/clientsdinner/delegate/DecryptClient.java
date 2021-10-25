package com.bancolombia.clientsdinner.delegate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "decrypt-service",url = "${feign.url}")
public interface DecryptClient {

    @GetMapping("/{clientCode}")
    public String decryptClientCode(@PathVariable("clientCode") String clientCode);
}
