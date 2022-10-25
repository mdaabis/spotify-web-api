package main.java.services;

import main.java.utils.AuthorisationUtils;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class AuthorizationService
{

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(AuthorisationUtils.CLIENT_ID)
            .setClientSecret(AuthorisationUtils.CLIENT_SECRET)
            .setRedirectUri(AuthorisationUtils.redirectUri)
            .build();

    public String spotifyLogin() throws MalformedURLException {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toURL().toString();
    }

    public void redirect(String userCode, HttpServletResponse response) throws IOException
    {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode)
                .build();

        try
        {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Access token: "+spotifyApi.getAccessToken());
    }
}
