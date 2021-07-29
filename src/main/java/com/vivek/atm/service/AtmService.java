package com.vivek.atm.service;

import com.vivek.atm.exception.DepositAmountZeroException;
import com.vivek.atm.exception.IncorrectDepositAmountException;
import com.vivek.atm.exception.IncorrectOrInsufficientFundsException;
import com.vivek.atm.model.BalanceDenominations;
import com.vivek.atm.repository.Denomination;

import java.math.BigDecimal;
import java.util.List;

public interface AtmService {
    void validateDeposit(List<Denomination> depositRequest) throws IncorrectDepositAmountException, DepositAmountZeroException;

    void makeDeposits(List<Denomination> depositRequest);

    BalanceDenominations getBalanceDenominations();

    void validateWithdrawal(BigDecimal withdrawRequest) throws IncorrectOrInsufficientFundsException;

    List<Denomination> makeWithdrawal(BigDecimal withdrawalAmount);
}
