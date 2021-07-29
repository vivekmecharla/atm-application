package com.vivek.atm.controller;

import com.vivek.atm.model.BalanceDenominations;
import com.vivek.atm.model.WithdrawResponse;
import com.vivek.atm.repository.Denomination;
import com.vivek.atm.service.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("atmService")
@RestController
public class AtmController {
    @Autowired
    private AtmService atmService;

    @PostMapping("/deposit")
    public BalanceDenominations deposit(@RequestBody List<Denomination> depositRequest) {
        atmService.validateDeposit(depositRequest);
        atmService.makeDeposits(depositRequest);
        return atmService.getBalanceDenominations();
    }

    @PostMapping("/withdraw")
    public WithdrawResponse withdraw(@RequestBody BigDecimal withdrawalAmount) {
        atmService.validateWithdrawal(withdrawalAmount);
        List<Denomination> dispensedDenominations = atmService.makeWithdrawal(withdrawalAmount);
        BalanceDenominations balanceDenominations = atmService.getBalanceDenominations();
        return new WithdrawResponse(dispensedDenominations, balanceDenominations.getBalances(), balanceDenominations.getTotal());
    }
}
