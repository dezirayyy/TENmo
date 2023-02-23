package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    // public BigDecimal viewBalance(int id);

    public Account get(int id);

    public List<Account> list(int id);



}
