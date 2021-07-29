package com.vivek.atm.model;

import java.math.BigDecimal;

public enum DenominationEnum {

    TWENTIES(BigDecimal.valueOf(20)),
    TENS(BigDecimal.valueOf(10)),
    FIVES(BigDecimal.valueOf(5)),
    ONES(BigDecimal.valueOf(1));

    private BigDecimal value;

    DenominationEnum(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

}
