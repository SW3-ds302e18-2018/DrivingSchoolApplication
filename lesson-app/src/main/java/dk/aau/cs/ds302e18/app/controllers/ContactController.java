package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.Notification;
import dk.aau.cs.ds302e18.app.service.AccountService;
import dk.aau.cs.ds302e18.app.domain.StoreModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class ContactController {
  
    private final AccountService accountService;

    public ContactController(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    @GetMapping(value = "/contact")
    public String getContactPage() {
        return "contact-formular";
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public RedirectView acceptContactState(@RequestParam("firstName") String firstName, @RequestParam("email") String email,
                                           @RequestParam("message") String message) {
        String sendMessage = ("New Email from : " + firstName + " \n" + "Email :" + email + " \n" + "Message : " + message);
        new Notification(sendMessage, "ds302e18@gmail.com", email);
        return new RedirectView("contact");
    }

    @ModelAttribute("gravatar")
    @PreAuthorize("isAuthenticated()")
    public String gravatar() {
        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/"+md5Hex(accountService.getAccount(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }

}