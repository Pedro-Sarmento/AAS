package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController{

    @GetMapping("/")
    public String home(){
        return "Login-Register";
    }

    @GetMapping("/index.html")
    public String index(){ return "index";}
}
