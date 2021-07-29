package com.vivek.atm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect Deposit Amount")
public class IncorrectDepositAmountException extends RuntimeException {
    public IncorrectDepositAmountException() {
        super("Incorrect Deposit Amount");
    }
}
