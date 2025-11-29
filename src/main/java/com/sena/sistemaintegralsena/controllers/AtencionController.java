package com.sena.sistemaintegralsena.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AtencionController {

    @GetMapping("/atencion-individual")
    public String atencionIndividual() {
        return "atencion-individual";
    }
}
