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

    public boolean sendBucks(int id, BigDecimal amount) {
        Account toAccount = getAccountByUserId(id);
        Account fromAccount = getAccountByUserId(currentUser.getUser().getId());

        boolean sufficientFunds = false;
        int result = fromAccount.getBalance().compareTo(amount);
        if (result < 1) {
            return sufficientFunds;
        } else {
            Transfer transfer = new Transfer();
            transfer.setAmount(amount);
            transfer.setAccount_from(fromAccount.getAccount_id());
            transfer.setAccount_to(toAccount.getAccount_id());
            transfer.setTransfer_type_id(2);
            transfer.setTransfer_status_id(2);


            try {
                HttpEntity<Transfer> entity = makeTransferEntity(transfer);
                ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/send", HttpMethod.POST, entity, Transfer.class);
                transfer = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
            return sufficientFunds = true;
        }
    }

//    public void requestBucks(int id, BigDecimal amount) {
//
//    }

        public Transfer[] viewTransferHistory ( int id){
            Transfer[] transferList = new Transfer[]{};
            try {
                ResponseEntity<Transfer[]> response =
                        restTemplate.exchange(baseUrl + "/" + id + "/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
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

    public Account getAccountByUserId(int id){
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + id, HttpMethod.GET, makeAuthEntity(), Account.class);
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
