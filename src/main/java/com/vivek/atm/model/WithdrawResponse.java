package com.vivek.atm.model;

import com.vivek.atm.repository.Denomination;

import java.math.BigDecimal;
import java.util.List;

public class WithdrawResponse extends BalanceDenominations {
    private List<Denomination> dispensed;

    public WithdrawResponse(List<Denomination> dispensed, List<Denomination> balances, BigDecimal total) {
        super(balances, total);
        this.dispensed = dispensed;
    }

    public List<Denomination> getDispensed() {
        return dispensed;
    }

    public void setDispensed(List<Denomination> dispensed) {
        this.dispensed = dispensed;
    }
}
