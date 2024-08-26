package com.olegastakhov.microservices.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

    @GetMapping("/gw")
    public Mono<String> quiz() {
        return Mono.just("Gateway");
    }

}
