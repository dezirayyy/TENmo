package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private AuthenticatedUser currentUser;

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
    public TransferService(String url) {
        this.baseUrl = url;
    }

    public void sendBucks(int id, BigDecimal amount){
        Account toAccount =  getAccount(id);
        Account fromAccount = getAccount(currentUser.getUser().getId());

        if (id == 0)
        if (fromAccount.getUser_id() == id) {
            throw new IllegalArgumentException("Cannot send bucks to yourself");
        }

        boolean sufficientFunds = false;
        int result = fromAccount.getBalance().compareTo(amount);
        if (result > -1) {
             sufficientFunds = true;
        } else {
            throw new IllegalStateException("Account does not have sufficient funds");
        }

        Transfer currentTransfer = new Transfer();
        currentTransfer.setAmount(amount);
        currentTransfer.setAccount_from(fromAccount.getAccount_id());
        currentTransfer.setAccount_to(toAccount.getAccount_id());
        currentTransfer.setTransfer_type_id(2);
        currentTransfer.setTransfer_status_id(2);

        if (sufficientFunds) {
            try {
                HttpEntity<Transfer> entity = makeTransferEntity(currentTransfer);
                ResponseEntity<Transfer> response =  restTemplate.exchange(baseUrl + "/send", HttpMethod.POST, entity, Transfer.class);
                currentTransfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
    }

    public Transfer[] viewTransferHistory(int id) {
        Transfer[] transferList = new Transfer[]{};
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseUrl +"/"+ id + "/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (transferList == null) {
            throw new NullPointerException("No Transfers found");
        } else {
            return transferList;
        }

    }

    public Transfer viewTransferDetails(int id){
        Transfer transfer = new Transfer(){};
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl + "/details/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (transfer == null) {
            throw new NullPointerException("No Transfer found");
        } else {
            return transfer;
        }
    }

    public Account getAccount(int id){
        Account account = null;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl + id,HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (account == null) {
            throw new NullPointerException("Account Not Found");
        } else {
            return account;
        }
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

}
