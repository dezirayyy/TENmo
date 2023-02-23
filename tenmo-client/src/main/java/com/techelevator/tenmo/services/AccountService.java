package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }
    public AccountService(String url) {
        this.baseUrl = url;
    }

    // returns a list of user ids and usernames, not including the current user

    public User[] listUsers(int id){
        User[] userList = new User[]{};
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(baseUrl + "list/" + id, HttpMethod.GET, makeAuthEntity(), User[].class);
            userList = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (userList == null) {
            throw new NullPointerException("No Accounts found");
        } else {
            return userList;
        }
    }


    // gets the balance of the current user
    public BigDecimal getBalance(int id){
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
            return account.getBalance();
        }
    }



    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(account, headers);
    }



    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
