package com.brunoshiroma.devtoolbelt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.brunoshiroma.devtoolbelt.config.DevtoolbeltConfigBean;

@Controller
public class StaticController extends AbstractController {

    public StaticController(DevtoolbeltConfigBean configBean) {
        super(configBean);
    }

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

    @GetMapping("/base64")
    public String base64(Model model){
        setUpModel(model);
        model.addAttribute(HTML_TITLE, " - Base64");
        model.addAttribute(HTML_DESCRIPTION, "Encode or decode base64 on your browser, secure without network activity.");
        return "base64.html";
    }

    @GetMapping("/url-encode")
    public String urlEncode(Model model){
        setUpModel(model);
        model.addAttribute(HTML_TITLE, " - URL encode");
        model.addAttribute(HTML_DESCRIPTION, "Encode or decode URL params on your browser, secure without network activity.");
        return "url-encode.html";
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