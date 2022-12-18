package com.brunoshiroma.devtoolbelt.services;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class NetworkToolsService {

    public String getClientIp(HttpServletRequest request){
        return request.getRemoteAddr();
    }

}