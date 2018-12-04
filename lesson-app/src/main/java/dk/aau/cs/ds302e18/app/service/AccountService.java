package dk.aau.cs.ds302e18.app.service;

import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.domain.AccountModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/* Class responsible for reading account related data from the 8100 server. */
@Service
public class AccountService
{
    private static final String ACCOUNT = "/account";
    private static final String SLASH = "/";

    @Value("${ds.service.url}")
    private String accountServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /* Retrieves an list of accounts from the 8100 server and returns it as list of accounts in the format specified in
       the account class. */
    public List<Account> getAllAccounts()
    {
        String url = accountServiceUrl + ACCOUNT;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Account>>() { }).getBody();
    }

    /* Returns an account object from the 8100 server that has just been added */
    public Account addAccount(AccountModel accountModel)
    {
        String url = accountServiceUrl + ACCOUNT;
        HttpEntity<AccountModel> request = new HttpEntity<>(accountModel, null);
        return this.restTemplate.exchange(url, HttpMethod.POST, request, Account.class).getBody();
    }


    public Account getAccount(String username) {
        String url = accountServiceUrl + ACCOUNT + SLASH + username;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Account.class).getBody();
    }

    public Account updateAccount(String username, AccountModel accountModel) {
        String url = accountServiceUrl + ACCOUNT + SLASH + username;
        HttpEntity<AccountModel> request = new HttpEntity<>(accountModel, null);
        return this.restTemplate.exchange(url, HttpMethod.PUT, request, Account.class).getBody();
    }

    public Account deleteAccount(String username)
    {
        String url = accountServiceUrl + ACCOUNT + SLASH + username;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.DELETE, request, Account.class).getBody();
    }
}

