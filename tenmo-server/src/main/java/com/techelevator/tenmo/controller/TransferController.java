package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private AccountDao dao;

    private TransferDao currentTransfer;

    public TransferController(AccountDao dao, TransferDao transfer) {
        this.dao = dao;
        this.currentTransfer = transfer;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable int id) {
        Account account = dao.get(id);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/{id}/send", method = RequestMethod.POST)
    public void sendBucks(@PathVariable int id, @Valid @RequestBody Transfer transfer) {
        if(!currentTransfer.add(transfer)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Transfer failed");
        }
    }
}
