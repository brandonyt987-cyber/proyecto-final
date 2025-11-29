package com.sena.sistemaintegralsena.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComiteController {

    @GetMapping("/comite")
    public String comite() {
        return "comite";
    }
}
