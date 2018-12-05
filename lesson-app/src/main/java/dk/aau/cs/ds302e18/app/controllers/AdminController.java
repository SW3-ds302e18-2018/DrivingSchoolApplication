package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.*;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.domain.AccountModel;
import dk.aau.cs.ds302e18.app.domain.AccountViewModel;
import dk.aau.cs.ds302e18.app.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
public class AdminController {
    private final AccountService accountService;
    private final AuthGroupRepository authGroupRepository;
    private final UserRepository userRepository;

    public AdminController(AccountService accountService, AuthGroupRepository authGroupRepository,
                           UserRepository userRepository) {
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getAdminPage(Model model) {
        List<AccountViewModel> accountViewModelList = new ArrayList<>();

        List<User> userArrayList = this.userRepository.findAll();
        List<Account> accounts = this.accountService.getAllAccounts();
        List<AuthGroup> authGroups = this.authGroupRepository.findAll();

        for (int i = 0; i < userArrayList.size(); i++) {
            AccountViewModel accountViewModel = new AccountViewModel();
            accountViewModel.setUsername(userArrayList.get(i).getUsername());
            accountViewModel.setFirstName(accounts.get(i).getFirstName());
            accountViewModel.setLastName(accounts.get(i).getLastName());
            accountViewModel.setAuthGroup(authGroups.get(i).getAuthGroup());
            accountViewModelList.add(accountViewModel);
        }

        model.addAttribute("GetAllAccounts", accountViewModelList);
        return "admin";
    }

    @GetMapping(value = "/admin/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAdminPageForUser(Model model, @PathVariable String username) {
        model.addAttribute("user", accountService.getAccount(username));
        model.addAttribute("userName", username);
        return "admin-edituser";
    }

    @RequestMapping(value = "/adminChangeDetails", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView changeAccountDetailsAdmin(@RequestParam("Username") String username,
                                                  @RequestParam("FirstName") String firstName,
                                                  @RequestParam("LastName") String lastName,
                                                  @RequestParam("Email") String email,
                                                  @RequestParam("PhoneNumber") String phoneNumber,
                                                  @RequestParam("Birthday") String birthday,
                                                  @RequestParam("Address") String address,
                                                  @RequestParam("City") String city,
                                                  @RequestParam("Zip") int zip) {
        Account account = new Account();
        account.setUsername(username);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);
        account.setPhoneNumber(phoneNumber);
        account.setBirthday(birthday);
        account.setAddress(address);
        account.setCity(city);
        account.setZipCode(zip);
        AccountModel accountModel = account.translateAccountToModel();
        this.accountService.updateAccount(username, accountModel);
        return new RedirectView("admin");
    }

    @RequestMapping(value = "/adminChangePassword", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView resetAccountPassword(
            @RequestParam("Username") String username,
            @RequestParam("Password") String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

        String newPass = passwordEncoder.encode(password);

        User user = new User();
        user.setId(userRepository.findByUsername(username).getId());
        user.setUsername(username);
        user.setPassword(newPass);
        user.setActive(true);
        this.userRepository.save(user);
        return new RedirectView("admin");
    }

    @RequestMapping(value = "/adminChangeRole", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView changeRole(
            @RequestParam("Username") String username,
            @RequestParam("Role") String role) {
        AuthGroup authGroup = new AuthGroup();
        authGroup.setId(userRepository.findByUsername(username).getId());
        authGroup.setAuthGroup(role);
        authGroup.setUsername(username);
        this.authGroupRepository.save(authGroup);
        return new RedirectView("admin");
    }


    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/" + md5Hex(accountService.getAccount(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }
}