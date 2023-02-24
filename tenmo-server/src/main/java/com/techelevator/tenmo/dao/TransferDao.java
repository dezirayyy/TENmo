package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public boolean save(Transfer transfer);

    public List<Transfer> listTransfers(int id);

    public List<Transfer> pendingTransfers(int id);

    Transfer getTransfer(int id);
}
