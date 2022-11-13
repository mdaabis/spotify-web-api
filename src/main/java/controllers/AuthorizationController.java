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
public class AuthorizationController
{
    private final AuthorizationService authorizationService = new AuthorizationService();

    @GetMapping(value = "/user")
    public ModelAndView user() throws MalformedURLException {
        return new ModelAndView(new RedirectView(authorizationService.spotifyLogin()));
    }

    @GetMapping(value = "/redirect")
    public String getRedirectCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException
    {
        return authorizationService.redirect(userCode, response);
    }
}
