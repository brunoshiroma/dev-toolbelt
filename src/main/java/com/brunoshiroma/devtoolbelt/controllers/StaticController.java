package com.brunoshiroma.devtoolbelt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController extends AbstractController {

    @GetMapping("/")
    public String index(Model model){
        setUpModel(model);
        model.addAttribute(HTML_TITLE, "");
        model.addAttribute(HTML_DESCRIPTION, "Devtoolbelt, simple tools for developer, trying to make life easier");
        return "index.html";
    }

    @GetMapping("/password")
    public String password(Model model){
        setUpModel(model);
        model.addAttribute(HTML_TITLE, " - Password");
        model.addAttribute(HTML_DESCRIPTION, "Generate Password on your browser, secure without network activity.");
        return "password.html";
    }

    @GetMapping("/sha")
    public String sha(Model model){
        setUpModel(model);
        model.addAttribute(HTML_TITLE, " - SHA");
        model.addAttribute(HTML_DESCRIPTION, "Generate SHA on your browser, secure without network activity.");
        return "sha.html";
    }

    @GetMapping("/offline")
    public String offline(Model model){
        setUpModel(model);
        return "offline.html";
    }

    @GetMapping("/service-worker.js")
    public String manifestJson(Model model){
        setUpModel(model);
        return "service-worker.js";
    }

}