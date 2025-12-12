package com.sena.sistemaintegralsena.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarCorreoRecuperacion(String destino, String token) {
        
        String url = "http://localhost:8082/restaurar-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@nexus-sena.edu.co");
        message.setTo(destino);
        message.setSubject("Recuperación de Contraseña | NEXUS");
        message.setText("Hola,\n\n" +
                "Hemos recibido una solicitud para restablecer tu contraseña en el Sistema NEXUS.\n" +
                "Haz clic en el siguiente enlace para crear una nueva contraseña:\n\n" +
                url + "\n\n" +
                "Este enlace expirará en 1 hora.\n" +
                "Si no solicitaste esto, ignora este mensaje.");
        
        mailSender.send(message);
    }
}