package com.vivek.atm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Deposit amount cannot be zero")
public class DepositAmountZeroException extends RuntimeException {
    public DepositAmountZeroException() {
        super("Deposit amount cannot be zero");
    }
}
