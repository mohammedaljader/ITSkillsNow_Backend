package com.itskillsnow.apigateway.filter;


import com.itskillsnow.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    private static final String BEARER_PREFIX = "Bearer ";

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
                }

                String authHeader = Objects.requireNonNull(exchange.getRequest()
                        .getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
                if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                    authHeader = authHeader.substring(BEARER_PREFIX.length());
                }
                try {
                    jwtUtil.validateToken(authHeader);

                    List<String> roles = jwtUtil.extractRoles(authHeader);

                    Mono<Void> exchangeResult = routingUser(exchange, chain, roles);

                    if (exchangeResult != null){
                        return exchangeResult;
                    }

                } catch (Exception e) {
                    System.out.println("Invalid access...!");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
                }
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> routingUser(ServerWebExchange exchange, GatewayFilterChain chain, List<String> roles) {
        boolean valid;
        if (roles.contains("ADMIN")) {
            valid = validator.isAdmin.test(exchange.getRequest());
        } else if (roles.contains("COMPANY")) {
            valid = validator.isCompany.test(exchange.getRequest());
        } else if (roles.contains("USER")) {
            valid = validator.isUser.test(exchange.getRequest());
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        if (valid) {
            return chain.filter(exchange);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }

    public static class Config {
    }
}
