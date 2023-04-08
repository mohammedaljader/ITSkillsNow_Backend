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

            if(roles == null){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if(validator.isAdmin.test(exchange.getRequest())){
                if (!roles.contains("ADMIN")) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }else {
                    return chain.filter(exchange);
                }
            }

            if(validator.isUser.test(exchange.getRequest())){
                if (!roles.contains("USER")) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }else {
                    return chain.filter(exchange);
                }
            }
        }
        return chain.filter(exchange);
    }
}
