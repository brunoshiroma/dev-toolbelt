package com.brunoshiroma.devtoolbelt.services;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@Component
public class NetworkToolsService {

    public String getClientIp(HttpServletRequest request){
        return request.getRemoteAddr();
    }

}