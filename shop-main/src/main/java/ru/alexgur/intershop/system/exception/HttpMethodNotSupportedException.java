package ru.alexgur.intershop.system.exception;

public class HttpMethodNotSupportedException extends RuntimeException {
    public HttpMethodNotSupportedException(String message) {
        super(message);
    }
}