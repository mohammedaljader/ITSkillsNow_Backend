package com.itskillsnow.apigateway.filter;

import com.itskillsnow.apigateway.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.List;


@Component
public class AuthorizationFilter implements WebFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    private final JwtUtil jwtUtil;

    private final RouteValidator validator;

    public AuthorizationFilter(JwtUtil jwtUtil, RouteValidator validator) {
        this.jwtUtil = jwtUtil;
        this.validator = validator;
    }


    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange,  @NonNull WebFilterChain chain) {
        if (validator.isSecured.test(exchange.getRequest())) {
            ServerHttpRequest request = exchange.getRequest();
            String authorizationHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

            if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            List<String> roles = jwtUtil.extractRoles(token);

            Mono<Void> exchangeResult = routingUser(exchange, chain, roles);

            if (exchangeResult != null){
                return exchangeResult;
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> routingUser(ServerWebExchange exchange, WebFilterChain chain, List<String> roles) {
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
}
