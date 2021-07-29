package com.vivek.atm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect or insufficient funds")
public class IncorrectOrInsufficientFundsException extends RuntimeException {
    public IncorrectOrInsufficientFundsException() {
        super("Incorrect or insufficient funds");
    }
}
