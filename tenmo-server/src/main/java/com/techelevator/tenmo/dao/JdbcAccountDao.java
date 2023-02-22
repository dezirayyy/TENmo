package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal viewBalance(int id) {
        BigDecimal balance;
        try {
            balance = jdbcTemplate.queryForObject("SELECT balance FROM account WHERE id = ?;", BigDecimal.class, id);
        } catch (Exception e) {
            return BigDecimal.valueOf(0);
        }
        return balance;
    }
}
