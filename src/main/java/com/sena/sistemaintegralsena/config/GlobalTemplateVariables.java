package com.sena.sistemaintegralsena.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalTemplateVariables {

    @ModelAttribute("request")
    public HttpServletRequest request(HttpServletRequest request) {
        return request;
    }

    @ModelAttribute("response")
    public HttpServletResponse response(HttpServletResponse response) {
        return response;
    }

    @ModelAttribute("session")
    public HttpSession session(HttpSession session) {
        return session;
    }
}
