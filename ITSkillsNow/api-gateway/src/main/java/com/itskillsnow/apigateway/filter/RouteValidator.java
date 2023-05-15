package com.itskillsnow.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // URL: /api/ServiceName/open/**
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/validate",
            "/eureka"
    );

    // URL: /api/ServiceName/admin/**
    public static final List<String> adminEndpoints = List.of(
            "/api/course",
            "/api/job"
    );

    // URL: /api/ServiceName/company/**
    public static final List<String> companyEndpoints = List.of(
            "/api/course",
            "/api/job"
    );

    // URL: /api/ServiceName/user/**
    public static final List<String> userEndpoints = List.of(
            "/api/job/messages",
            "/auth/deleteMe"
    );


    public Predicate<ServerHttpRequest> isAdmin =
            request -> adminEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isCompany =
            request -> companyEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isUser =
            request -> userEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
