package com.brunoshiroma.devtoolbelt.services;

import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class NetworkToolsService {

    public String getClientIp(HttpServletRequest request) {
        final var forwardedHeader = request.getHeader("X-Forwarded-For");

        return Optional.of(forwardedHeader).orElse(request.getRemoteAddr());
    }

}