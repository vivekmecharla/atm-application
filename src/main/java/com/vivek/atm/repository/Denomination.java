package com.vivek.atm.repository;

import com.vivek.atm.model.DenominationEnum;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Denomination {
    @Id
    private DenominationEnum denominationValue;
    private BigDecimal denominationCount;

    public Denomination() {
    }

    public Denomination(DenominationEnum denominationValue, BigDecimal denominationCount) {
        this.denominationValue = denominationValue;
        this.denominationCount = denominationCount;
    }

    public BigDecimal getDenominationCount() {
        return denominationCount;
    }

    public void setDenominationCount(BigDecimal count) {
        this.denominationCount = count;
    }

    public DenominationEnum getDenominationValue() {
        return denominationValue;
    }

    public void setDenominationValue(DenominationEnum denominationValue) {
        this.denominationValue = denominationValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Denomination that = (Denomination) o;
        return denominationValue.getValue().equals(that.denominationValue.getValue())  && Objects.equals(denominationCount, that.denominationCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(denominationValue, denominationCount);
    }
}
