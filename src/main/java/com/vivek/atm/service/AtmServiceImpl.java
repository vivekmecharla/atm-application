package com.vivek.atm.service;

import com.vivek.atm.exception.*;
import com.vivek.atm.model.BalanceDenominations;
import com.vivek.atm.model.DenominationEnum;
import com.vivek.atm.repository.Denomination;
import com.vivek.atm.repository.DenominationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AtmServiceImpl implements AtmService {
    @Autowired
    private DenominationRepository denominationRepository;

    @Override
    public BalanceDenominations getBalanceDenominations() {
        List<Denomination> balances = denominationRepository.findAll();
        BigDecimal totalBalance = getTotalBalance(balances);
        return new BalanceDenominations(balances, totalBalance);
    }

    @Override
    public void validateWithdrawal(BigDecimal withdrawalAmount) throws IncorrectOrInsufficientFundsException {
        BalanceDenominations balanceDenominations = getBalanceDenominations();
        BigDecimal total = balanceDenominations.getTotal();
        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0
                || withdrawalAmount.compareTo(total) > 0) {
            throw new IncorrectOrInsufficientFundsException();
        }
    }

    @Override
    @Transactional
    public List<Denomination> makeWithdrawal(BigDecimal withdrawalAmount) {
        BalanceDenominations balanceDenominations = getBalanceDenominations();
        List<Denomination> dispensedDenominations = new ArrayList<>();
        for (Denomination balanceDenomination : balanceDenominations.getBalances()) {
            DenominationEnum denominationEnum = balanceDenomination.getDenominationValue();
            BigDecimal denominationValue = denominationEnum.getValue();

            BigDecimal denominationCount = withdrawalAmount.divideToIntegralValue(denominationValue);
            if (balanceDenomination.getDenominationCount().compareTo(BigDecimal.ZERO) > 0) {
                if (denominationCount.compareTo(balanceDenomination.getDenominationCount()) > 0) {
                    denominationCount = balanceDenomination.getDenominationCount();
                }
            }

            Denomination denominationToBeUpdated = new Denomination(denominationEnum, balanceDenomination.getDenominationCount().subtract(denominationCount));
            denominationRepository.save(denominationToBeUpdated);

            Denomination dispensedDenomination = new Denomination(denominationEnum, denominationCount);
            dispensedDenominations.add(dispensedDenomination);
            withdrawalAmount = withdrawalAmount.subtract(denominationValue.multiply(denominationCount));
            if (withdrawalAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
        if (withdrawalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return dispensedDenominations;
        } else {
            throw new NotDispensableException();
        }
    }

    private BigDecimal getTotalBalance(List<Denomination> balances) {
        double totalBalance = balances.stream()
                .mapToDouble(denomination ->
                        denomination.getDenominationCount().multiply(denomination.getDenominationValue().getValue()).doubleValue())
                .sum();
        return BigDecimal.valueOf(totalBalance);
    }

    @Override
    @Transactional
    public void makeDeposits(List<Denomination> depositRequest) {
        for (Denomination denomination : depositRequest) {
            Optional<Denomination> optionalDenomination = denominationRepository.findById(denomination.getDenominationValue());
            if (optionalDenomination.isPresent()) {
                Denomination denominationToBeUpdated = optionalDenomination.get();
                denominationToBeUpdated.setDenominationCount(denominationToBeUpdated.getDenominationCount().add(denomination.getDenominationCount()));
                denominationRepository.save(denominationToBeUpdated);
            } else {
                denominationRepository.save(denomination);
            }
        }
    }

    @Override
    public void validateDeposit(List<Denomination> depositRequest) throws IncorrectDepositAmountException, DepositAmountZeroException {
        if (depositRequest.stream().anyMatch(denomination -> denomination.getDenominationCount().compareTo(BigDecimal.ZERO) < 0)) {
            throw new IncorrectDepositAmountException();
        }
        double totalCount = depositRequest.stream().mapToDouble(value -> value.getDenominationCount().doubleValue()).sum();
        if (totalCount == 0) {
            throw new DepositAmountZeroException();
        }
    }

}
