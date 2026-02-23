package com.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class BibliotecaException extends RuntimeException {

    private final HttpStatus status;

    public BibliotecaException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // Atajos de uso frecuente
    public static BibliotecaException notFound(String mensaje) {
        return new BibliotecaException(mensaje, HttpStatus.NOT_FOUND);
    }

    public static BibliotecaException conflict(String mensaje) {
        return new BibliotecaException(mensaje, HttpStatus.CONFLICT);
    }

    public static BibliotecaException badRequest(String mensaje) {
        return new BibliotecaException(mensaje, HttpStatus.BAD_REQUEST);
    }
}
