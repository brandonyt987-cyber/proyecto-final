package com.sena.sistemaintegralsena.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

@Controller
public class ErrorControllerCustom implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        model.addAttribute("status", status != null ? status.toString() : "Desconocido");
        model.addAttribute("message", message != null ? message.toString() : "No hay mensaje");

        // Convertimos el stacktrace a string para mostrarlo en el HTML
        if (exception != null && exception instanceof Throwable throwable) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            model.addAttribute("stackTrace", sw.toString());
        } else {
            model.addAttribute("stackTrace", "Ninguna");
        }

        return "error"; // tu error.html
    }
}
