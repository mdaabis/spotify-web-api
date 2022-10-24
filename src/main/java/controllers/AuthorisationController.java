package main.java.controllers;

import main.java.services.AuthorisationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorisationController {

    @GetMapping(value = "/user")
    public void user(){
        AuthorisationService.spotifyLogin();
    }
}
