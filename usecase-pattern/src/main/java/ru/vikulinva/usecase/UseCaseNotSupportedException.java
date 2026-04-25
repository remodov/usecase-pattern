package ru.vikulinva.usecase;

public class UseCaseNotSupportedException extends RuntimeException {

    public UseCaseNotSupportedException(String message) {
        super(message);
    }

}