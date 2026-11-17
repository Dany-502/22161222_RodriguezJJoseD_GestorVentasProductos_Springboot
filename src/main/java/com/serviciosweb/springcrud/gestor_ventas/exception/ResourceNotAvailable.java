package com.serviciosweb.springcrud.gestor_ventas.exception;

public class ResourceNotAvailable extends RuntimeException {
    public ResourceNotAvailable(String message) {
        super(message);
    }
}
