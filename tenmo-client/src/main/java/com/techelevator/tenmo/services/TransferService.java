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

    public boolean sendBucks(int id, BigDecimal amount){
        Account toAccount =  getAccount(id);
        Account fromAccount = getAccount(currentUser.getUser().getId());

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
        currentTransfer.setTransfer_id(1);


        Transfer returnedTransfer = null;
        if (sufficientFunds) {
            try {
                ResponseEntity<Transfer> response =  restTemplate.exchange(baseUrl + id + "/send", HttpMethod.POST, makeAuthEntity(), Transfer.class);
                returnedTransfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }

        if (returnedTransfer == null){
            throw new UnsupportedOperationException("Transfer not successful");
        } else {
            return true;
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
