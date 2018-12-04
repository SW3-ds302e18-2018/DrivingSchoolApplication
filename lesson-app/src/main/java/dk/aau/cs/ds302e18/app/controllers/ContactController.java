package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.Notification;
import dk.aau.cs.ds302e18.app.service.AccountService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
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
        new Notification(sendMessage, email);
        return new RedirectView("contact");
    }

    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/"+md5Hex(accountService.getAccount(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername() {
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}