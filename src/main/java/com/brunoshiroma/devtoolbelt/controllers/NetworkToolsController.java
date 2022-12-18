package com.brunoshiroma.devtoolbelt.controllers;

import com.brunoshiroma.devtoolbelt.config.DevtoolbeltConfigBean;
import com.brunoshiroma.devtoolbelt.services.NetworkToolsService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NetworkToolsController extends AbstractController{

    private final NetworkToolsService networkToolsService;

    public NetworkToolsController(DevtoolbeltConfigBean configBean, NetworkToolsService networkToolsService) {
        super(configBean);
        this.networkToolsService = networkToolsService;
    }

    @GetMapping(value = "/network/ip")
    public ResponseEntity<String> getClientIp(HttpServletRequest request){
        return ResponseEntity.ok(networkToolsService.getClientIp(request));
    }

    @GetMapping(value = "/ip")
    public String getClientIpHTML(HttpServletRequest request, Model model){
        setUpModel(model);
        model.addAttribute("clientIp", networkToolsService.getClientIp(request));
        model.addAttribute(HTML_TITLE, " - Your IP");
        model.addAttribute(HTML_DESCRIPTION, "Check your public IP.");
        return "ip.html";
    }

}