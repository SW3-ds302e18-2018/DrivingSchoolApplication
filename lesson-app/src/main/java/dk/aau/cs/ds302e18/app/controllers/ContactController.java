package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.Notification;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.domain.StoreModel;
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

    private final AccountRespository accountRespository;

    public ContactController(AccountRespository accountRespository) {
        super();
        this.accountRespository = accountRespository;
    }

    @GetMapping(value = "/contact")
    public String getContactPage() {
        return "contact-formular";
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public RedirectView acceptContactState(@RequestParam("firstName") String firstName, @RequestParam("email") String email,
                                           @RequestParam("message") String message) {
        String sendmessage = ("New Email from : " + firstName + " \n" + "Email :" + email + " \n" + "Message : " + message);
        System.out.println( sendmessage + email);
        new Notification(sendmessage, email);
        return new RedirectView("contact");
    }

    @ModelAttribute("gravatar")
    public String gravatar() {

        //Models Gravatar
        System.out.println(accountRespository.findByUsername(getAccountUsername()).getEmail());
        String gravatar = ("http://0.gravatar.com/avatar/"+md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername()
    {
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }



}