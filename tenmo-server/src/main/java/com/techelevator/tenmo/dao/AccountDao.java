package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface AccountDao {
    // public BigDecimal viewBalance(int id);

    public Account get(int id);

    public List<User> listUsers(int id);



}
