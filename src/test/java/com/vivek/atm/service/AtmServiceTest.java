package com.vivek.atm.service;

import com.vivek.atm.exception.DepositAmountZeroException;
import com.vivek.atm.exception.IncorrectDepositAmountException;
import com.vivek.atm.exception.IncorrectOrInsufficientFundsException;
import com.vivek.atm.exception.NotDispensableException;
import com.vivek.atm.model.DenominationEnum;
import com.vivek.atm.repository.Denomination;
import com.vivek.atm.repository.DenominationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vivek.atm.model.DenominationEnum.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class AtmServiceTest {
    @InjectMocks
    private AtmServiceImpl atmService;

    @Mock
    private DenominationRepository denominationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testValidateDeposit() {
        List<Denomination> depositRequest = new ArrayList<>();
        depositRequest.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
        depositRequest.add(new Denomination(TENS, BigDecimal.valueOf(10)));
        depositRequest.add(new Denomination(DenominationEnum.FIVES, BigDecimal.valueOf(10)));
        depositRequest.add(new Denomination(DenominationEnum.ONES, BigDecimal.valueOf(10)));
        atmService.validateDeposit(depositRequest);
    }

    @Test
    void testValidateDeposit_IncorrectDepositAmount() {
        Exception exception = assertThrows(IncorrectDepositAmountException.class, () -> {
            List<Denomination> depositRequest = new ArrayList<>();
            depositRequest.add(new Denomination(TWENTIES, BigDecimal.valueOf(-100)));
            depositRequest.add(new Denomination(TENS, BigDecimal.valueOf(10)));
            depositRequest.add(new Denomination(DenominationEnum.FIVES, BigDecimal.valueOf(10)));
            depositRequest.add(new Denomination(DenominationEnum.ONES, BigDecimal.valueOf(10)));
            atmService.validateDeposit(depositRequest);
        });

        String expectedMessage = "Incorrect Deposit Amount";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testValidateDeposit_DepositAmountZero() {
        Exception exception = assertThrows(DepositAmountZeroException.class, () -> {
            List<Denomination> depositRequest = new ArrayList<>();
            depositRequest.add(new Denomination(TWENTIES, BigDecimal.valueOf(0)));
            depositRequest.add(new Denomination(TENS, BigDecimal.valueOf(0)));
            atmService.validateDeposit(depositRequest);
        });

        String expectedMessage = "Deposit amount cannot be zero";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMakeDeposits() {
        List<Denomination> depositRequest = new ArrayList<>();
        depositRequest.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
        depositRequest.add(new Denomination(TENS, BigDecimal.valueOf(10)));

        when(denominationRepository.findById(eq(TWENTIES))).thenReturn(Optional.of(new Denomination(TWENTIES, BigDecimal.valueOf(10))));
        when(denominationRepository.findById(eq(TENS))).thenReturn(Optional.empty());
        when(denominationRepository.save(any())).thenReturn(new Denomination(TWENTIES, BigDecimal.valueOf(10)));

        atmService.makeDeposits(depositRequest);
        verify(denominationRepository, times(2)).save(any());
    }

    @Test
    void testGetBalanceDenominations() {
        List<Denomination> balances = new ArrayList<>();
        balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
        balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));

        when(denominationRepository.findAll()).thenReturn(balances);

        assertEquals(atmService.getBalanceDenominations().getBalances(), balances);
        assertEquals(atmService.getBalanceDenominations().getTotal(), BigDecimal.valueOf(300.0));
    }

    @Test
    void testValidateWithdrawal() {
        List<Denomination> balances = new ArrayList<>();
        balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
        balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));

        when(denominationRepository.findAll()).thenReturn(balances);
        atmService.validateWithdrawal(BigDecimal.valueOf(100));
    }

    @Test
    void testValidateWithdrawal_IncorrectFunds() {
        Exception exception = assertThrows(IncorrectOrInsufficientFundsException.class, () -> {
            List<Denomination> balances = new ArrayList<>();
            balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
            balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));

            when(denominationRepository.findAll()).thenReturn(balances);
            atmService.validateWithdrawal(BigDecimal.valueOf(-100));
        });

        String expectedMessage = "Incorrect or insufficient funds";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testValidateWithdrawal_InsufficientFunds() {
        Exception exception = assertThrows(IncorrectOrInsufficientFundsException.class, () -> {
            List<Denomination> balances = new ArrayList<>();
            balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
            balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));

            when(denominationRepository.findAll()).thenReturn(balances);
            atmService.validateWithdrawal(BigDecimal.valueOf(1000));
        });

        String expectedMessage = "Incorrect or insufficient funds";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMakeWithdrawal_FewDenominations() {
        List<Denomination> balances = new ArrayList<>();
        balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(3)));
        balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));

        when(denominationRepository.findAll()).thenReturn(balances);
        List<Denomination> expectedDenominations = new ArrayList<>();
        expectedDenominations.add(new Denomination(TWENTIES, BigDecimal.valueOf(3)));
        expectedDenominations.add(new Denomination(TENS, BigDecimal.valueOf(4)));
        List<Denomination> actualDenominations = atmService.makeWithdrawal(BigDecimal.valueOf(100));
        assertEquals(expectedDenominations.size(), actualDenominations.size());
        for (int i = 0; i < expectedDenominations.size(); i++) {
            assertEquals(expectedDenominations.get(i), actualDenominations.get(i));
        }
    }

    @Test
    void testMakeWithdrawal_AllDenominations() {
        List<Denomination> balances = new ArrayList<>();
        balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(5)));
        balances.add(new Denomination(TENS, BigDecimal.valueOf(1)));
        balances.add(new Denomination(FIVES, BigDecimal.valueOf(10)));
        balances.add(new Denomination(ONES, BigDecimal.valueOf(5)));

        when(denominationRepository.findAll()).thenReturn(balances);
        when(denominationRepository.save(any())).thenReturn(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
        List<Denomination> expectedDenominations = new ArrayList<>();
        expectedDenominations.add(new Denomination(TWENTIES, BigDecimal.valueOf(5)));
        expectedDenominations.add(new Denomination(TENS, BigDecimal.valueOf(1)));
        expectedDenominations.add(new Denomination(FIVES, BigDecimal.valueOf(10)));
        expectedDenominations.add(new Denomination(ONES, BigDecimal.valueOf(5)));
        List<Denomination> actualDenominations = atmService.makeWithdrawal(BigDecimal.valueOf(165));
        assertEquals(expectedDenominations.size(), actualDenominations.size());
        for (int i = 0; i < expectedDenominations.size(); i++) {
            assertEquals(expectedDenominations.get(i), actualDenominations.get(i));
        }
    }

    @Test
    void testMakeWithdrawal_NotDispensable() {
        Exception exception = assertThrows(NotDispensableException.class, () -> {
            List<Denomination> balances = new ArrayList<>();
            balances.add(new Denomination(TWENTIES, BigDecimal.valueOf(5)));
            balances.add(new Denomination(TENS, BigDecimal.valueOf(10)));
            balances.add(new Denomination(FIVES, BigDecimal.valueOf(10)));
            balances.add(new Denomination(ONES, BigDecimal.valueOf(3)));

            when(denominationRepository.findAll()).thenReturn(balances);
            when(denominationRepository.save(any())).thenReturn(new Denomination(TWENTIES, BigDecimal.valueOf(10)));
            atmService.makeWithdrawal(BigDecimal.valueOf(169));
        });

        String expectedMessage = "Requested withdraw amount is not dispensable";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}