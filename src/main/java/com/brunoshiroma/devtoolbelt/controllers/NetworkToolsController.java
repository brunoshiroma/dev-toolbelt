package com.brunoshiroma.devtoolbelt.controllers;

import com.brunoshiroma.devtoolbelt.services.NetworkToolsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/network")
public class NetworkToolsController {

    private final NetworkToolsService networkToolsService;

    public NetworkToolsController(NetworkToolsService networkToolsService) {
        this.networkToolsService = networkToolsService;
    }

    @GetMapping("/ip")
    public ResponseEntity<String> getClientIp(HttpServletRequest request){
        return ResponseEntity.ok(networkToolsService.getClientIp(request));
    }

}