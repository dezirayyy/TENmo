package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Balance {

    // using Big Decimals to set decimal place to match prices
    private BigDecimal balance = BigDecimal.valueOf(0).setScale(2);

    public Balance(BigDecimal balance) {
        this.balance = balance;
    }

    // adds money to balance
    public void increase(int money) {
        balance = balance.add(BigDecimal.valueOf(money));
    }

    // subtracts money from balance
    public void decrease(BigDecimal money) {

        // checks to see if balance is enough to cover cost
        if (balance.compareTo(money) >= 0) {
            balance = balance.subtract(money);
        }
    }

    public BigDecimal getBalance() { return balance; }


    public void send(BigDecimal amount) {

    }

}
