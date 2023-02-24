package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

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
        Account account = dao.getAccount(id);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public void sendBucks(@Valid @RequestBody Transfer transfer) {
        if(!currentTransfer.sendBucks(transfer)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Transfer failed");
        }
        dao.updateBalances(transfer.getAccount_to(), transfer.getAccount_from(),transfer.getAmount(),transfer.getAmount());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/{id}/history", method = RequestMethod.GET)
    public Transfer[] viewTransferHistory(@PathVariable int id) {
        List<Transfer> list = currentTransfer.listTransfers(id);
        if (list == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfers Not Found");
        } else {
            return list.toArray(new Transfer[list.size()]);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/details/{id}", method = RequestMethod.GET)
    public Transfer viewTransferDetails(@PathVariable int id){
        Transfer transfer = currentTransfer.getTransfer(id);
        if (transfer == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
        } else {
            return transfer;
        }
    }
}
