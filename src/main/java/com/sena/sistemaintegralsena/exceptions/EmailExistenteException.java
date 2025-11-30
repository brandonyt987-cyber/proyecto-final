package com.sena.sistemaintegralsena.exceptions;

public class EmailExistenteException extends RuntimeException {
    
    private static final long serialVersionUID = 1L; 

    public EmailExistenteException(String message) {
        super(message);
    }
}