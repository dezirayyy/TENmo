package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public boolean sendBucks(Transfer transfer);

    public List<Transfer> listTransfers(int id);

    public Transfer getTransfer(int id);
}
