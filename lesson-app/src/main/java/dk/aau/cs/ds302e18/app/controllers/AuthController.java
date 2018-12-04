package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.*;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.service.AccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller responsible for handling actions towards the account
 */
@Controller
public class AuthController {
    private final AccountService accountService;
    private final AuthGroupRepository authGroupRepository;
    private final UserRepository userRepository;

    public AuthController(AccountService accountService, AuthGroupRepository authGroupRepository,
                          UserRepository userRepository) {
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/logout-success")
    public String getLogoutPage() {
        return "logout";
    }

    @GetMapping(value = "/register")
    public String getRegisterPage() {
        return "register-account";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public RedirectView registerAccount(@RequestParam("Username") String username,
                                        @RequestParam("Password") String password,
                                        @RequestParam("Email") String email,
                                        @RequestParam("FirstName") String firstName,
                                        @RequestParam("LastName") String lastName,
                                        @RequestParam("PhoneNumber") String phoneNumber,
                                        @RequestParam("Birthday") String birthday,
                                        @RequestParam("Address") String address,
                                        @RequestParam("City") String city,
                                        @RequestParam("Zip") int zip) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

        String newPass = passwordEncoder.encode(password);

        User user = new User();
        user.setActive(true);
        user.setUsername(username);
        user.setPassword(newPass);

        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPhoneNumber(phoneNumber);
        account.setBirthday(birthday);
        account.setAddress(address);
        account.setCity(city);
        account.setZipCode(zip);
        account.setNotificationInMinutes(120); // Default to 2 hours

        AuthGroup authGroup = new AuthGroup();
        authGroup.setUsername(username);
        authGroup.setAuthGroup("STUDENT");

        this.accountService.addAccount(account.translateAccountToModel());
        this.authGroupRepository.save(authGroup);
        this.userRepository.save(user);

        return new RedirectView("login");
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "login";
    }
}
