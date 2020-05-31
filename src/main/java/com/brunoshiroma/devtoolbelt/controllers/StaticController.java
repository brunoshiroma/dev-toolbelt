package com.brunoshiroma.devtoolbelt.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @Value("${release-date}")
    private String releaseDate;

    @GetMapping("/")
    public String index(){
        return "index.html";
    }

    @GetMapping("/password")
    public String password(){
        return "password.html";
    }

    @GetMapping("/sha")
    public String sha(){
        return "sha.html";
    }

    @GetMapping("/offline")
    public String offline(){
        return "offline.html";
    }

    @GetMapping("/service-worker.js")
    public String manifestJson(Model model){
        model.addAttribute("CACHE_VERSION", releaseDate);
        return "service-worker.js";
    }

}