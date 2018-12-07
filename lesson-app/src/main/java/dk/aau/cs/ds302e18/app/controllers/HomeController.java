package dk.aau.cs.ds302e18.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/homee")
    public String home() {
        return "Hello World!";
    }
}