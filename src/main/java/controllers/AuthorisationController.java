package main.java.controllers;

import main.java.services.AuthorizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
public class AuthorisationController
{

    private final AuthorizationService authorizationService = new AuthorizationService();

    @GetMapping(value = "/user")
    public ModelAndView user() throws MalformedURLException {
        return new ModelAndView(new RedirectView(authorizationService.spotifyLogin()));
    }

    @GetMapping(value = "/redirect")
    public void getRedirectCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException
    {
        authorizationService.redirect(userCode, response);
    }
}
