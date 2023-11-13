package it.unibo.bank.impl;

import it.unibo.bank.api.AccountHolder;
import it.unibo.bank.api.BankAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.unibo.bank.impl.SimpleBankAccount.*;
import static it.unibo.bank.impl.StrictBankAccount.TRANSACTION_FEE;

public class TestStrictBankAccount {

    private final static int INITIAL_AMOUNT = 100;

    // 1. Create a new AccountHolder and a StrictBankAccount for it each time tests
    // are executed.
    private AccountHolder mRossi;
    private BankAccount bankAccount;

    @BeforeEach
    public void setUp() {
        this.mRossi = new AccountHolder("Mario", "Rossi", 1);
        this.bankAccount = new StrictBankAccount(mRossi, INITIAL_AMOUNT);
    }

    // 2. Test the initial state of the StrictBankAccount
    @Test
    public void testInitialization() {
        assertEquals(mRossi, bankAccount.getAccountHolder());
        assertEquals(INITIAL_AMOUNT, bankAccount.getBalance());
        assertEquals(0, bankAccount.getTransactionsCount());
        assertEquals(1, mRossi.getUserID());
    }

    // 3. Perform a deposit of 100€, compute the management fees, and check that the
    // balance is correctly reduced.
    @Test
    public void testManagementFees() {
        assertEquals(0, bankAccount.getTransactionsCount());
        bankAccount.deposit(mRossi.getUserID(), 100);
        final double expectedBalance = INITIAL_AMOUNT + 100;
        assertEquals(expectedBalance, bankAccount.getBalance());
        assertEquals(1, bankAccount.getTransactionsCount());
        final double feeAmount = MANAGEMENT_FEE + 1 * TRANSACTION_FEE;
        bankAccount.chargeManagementFees(mRossi.getUserID());
        assertEquals(expectedBalance - feeAmount, bankAccount.getBalance());
        assertEquals(0, bankAccount.getTransactionsCount());
        try {
            bankAccount.chargeManagementFees(2);
            fail("Charged fees without authorization");
        } catch (IllegalArgumentException e) {
            assertEquals("ID not corresponding: cannot charge management fees", e.getMessage());
        }
    }

    // 4. Test the withdraw of a negative value
    @Test
    public void testNegativeWithdraw() {
        bankAccount.deposit(mRossi.getUserID(), 1000);
        try {
            bankAccount.withdraw(mRossi.getUserID(), -100);
            fail("Withdrown a negative amount");
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot withdraw a negative amount", e.getMessage());
        }
    }

    // 5. Test withdrawing more money than it is in the account
    @Test
    public void testWithdrawingTooMuch() {
        bankAccount.deposit(mRossi.getUserID(), 1000);
        try {
            bankAccount.withdraw(mRossi.getUserID(), 10000);
            fail("Withdrown more than the account balance");
        } catch (IllegalArgumentException e) {
            assertEquals("Insufficient balance", e.getMessage());
        }
    }
}
