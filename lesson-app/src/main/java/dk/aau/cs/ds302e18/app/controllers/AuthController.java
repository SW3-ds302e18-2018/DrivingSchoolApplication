package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller responsible for handling actions towards the account
 */
@Controller
public class AuthController
{

    private final AccountRespository accountRespository;
    private final AuthGroupRepository authGroupRepository;
    private final UserRepository userRepository;

    public AuthController(AccountRespository accountRespository, AuthGroupRepository authGroupRepository,
                             UserRepository userRepository) {
        this.accountRespository = accountRespository;
        this.authGroupRepository = authGroupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/logout-success")
    public String getLogoutPage(Model model)
    {
        return "logout";
    }

    @GetMapping(value = "/register")
    public String getRegisterPage()
    {
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
                                        @RequestParam("Zip") int zip)
    {
        System.out.println(username+password+email+firstName+lastName+phoneNumber+birthday+address+city+zip);

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

        AuthGroup authGroup = new AuthGroup();
        authGroup.setUsername(username);
        authGroup.setAuthGroup("STUDENT");

        accountRespository.save(account);
        authGroupRepository.save(authGroup);
        userRepository.save(user);

        return new RedirectView("login");
    }

    @GetMapping(value = "/login")
    public String getLoginPage(Model model)
    {
        return "login";
    }


    public String getAccountUsername()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return username;
    }


}
