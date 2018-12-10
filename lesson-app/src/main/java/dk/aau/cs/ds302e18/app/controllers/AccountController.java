package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.*;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.service.AccountService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Controller responsible for handling actions towards the account
 */
@Controller
public class AccountController {
    private final AccountService accountService;
    private final AuthGroupRepository authGroupRepository;
    private final UserRepository userRepository;

    public AccountController(AccountService accountService, AuthGroupRepository authGroupRepository,
                             UserRepository userRepository) {
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/account/edit")
    public String getManageAccount(Model model) {
        model.addAttribute("user", accountService.getAccount(getAccountUsername()));
        model.addAttribute("userAuth",
                authGroupRepository.findByUsername(getAccountUsername()).get(0).getAuthGroup());
        return "manage-account";
    }

    /**
     * @param firstName   The first name of the user
     * @param lastName    The last name of the user
     * @param email       The email of the user
     * @param phoneNumber The phone number of the user
     * @param birthday    The birthday of the user
     * @param address     The address of the user
     * @param city        The city of the user
     * @param zip         The zip of the user
     * @return The site to be redirected to
     */
    @RequestMapping(value = "/account/edit/details", method = RequestMethod.POST)
    public RedirectView changeAccountDetails(@RequestParam("FirstName") String firstName,
                                             @RequestParam("LastName") String lastName,
                                             @RequestParam("Email") String email,
                                             @RequestParam("PhoneNumber") String phoneNumber,
                                             @RequestParam("Birthday") String birthday,
                                             @RequestParam("Address") String address,
                                             @RequestParam("City") String city,
                                             @RequestParam("Zip") int zip,
                                             @RequestParam("NotificationInMinutes") int notificationInMinutes) {
        // Retrieve account from the repository

        Account account = accountService.getAccount(getAccountUsername());
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);
        account.setPhoneNumber(phoneNumber);
        account.setBirthday(birthday);
        account.setAddress(address);
        account.setCity(city);
        account.setZipCode(zip);
        account.setNotificationInMinutes(notificationInMinutes);
        this.accountService.updateAccount(account.getUsername(),account.translateAccountToModel());
        return new RedirectView("redirect:/account/edit");
    }

    @RequestMapping(value = "/account/edit/password", method = RequestMethod.POST)
    public RedirectView changeAccountPassword(@RequestParam("Password") String password) {
        // Get current user by username
        User user = userRepository.findByUsername(getAccountUsername());

        // Initialize BCryptPasswordEncoder with strength 11
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

        // Encode and store the password in the variable
        String encodedPassword = passwordEncoder.encode(password);

        // Replace the user's password with the new encoded password
        user.setPassword(encodedPassword);

        // Save the user back into the repository
        this.userRepository.save(user);
        return new RedirectView("redirect:/account/edit");
    }

    @RequestMapping(value = "/account/edit/delete", method = RequestMethod.POST)
    public ModelAndView deleteAccount() {
        User user = this.userRepository.findByUsername(getAccountUsername());
        this.userRepository.deleteById(user.getId());
        this.accountService.deleteAccount(user.getUsername());
        this.authGroupRepository.deleteById(user.getId());
        return new ModelAndView("redirect:/logout");
    }


    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        return "http://0.gravatar.com/avatar/" + md5Hex(accountService.getAccount(getAccountUsername()).getEmail());
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }
}
