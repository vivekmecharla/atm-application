package com.vivek.atm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK, reason = "Requested withdraw amount is not dispensable")
public class NotDispensableException extends RuntimeException {
    public NotDispensableException() {
        super("Requested withdraw amount is not dispensable");
    }
}
