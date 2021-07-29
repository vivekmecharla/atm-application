package com.vivek.atm.model;

import com.vivek.atm.repository.Denomination;

import java.math.BigDecimal;
import java.util.List;

public class BalanceDenominations {
    private List<Denomination> balances;
    private BigDecimal total;

    public BalanceDenominations(List<Denomination> balances, BigDecimal total) {
        this.balances = balances;
        this.total = total;
    }

    public List<Denomination> getBalances() {
        return balances;
    }

    public void setBalances(List<Denomination> balances) {
        this.balances = balances;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
