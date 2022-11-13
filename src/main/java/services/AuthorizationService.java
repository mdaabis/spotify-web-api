package main.java.services;

import main.java.models.AuthorizationSingleton;
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

public class AuthorizationService
{
    public static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(AuthorisationUtils.CLIENT_ID)
            .setClientSecret(AuthorisationUtils.CLIENT_SECRET)
            .setRedirectUri(AuthorisationUtils.REDIRECT_URI)
            .build();

    public String spotifyLogin() throws MalformedURLException
    {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(AuthorisationUtils.SCOPES)
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toURL().toString();
    }

    public String redirect(String userCode, HttpServletResponse response) throws IOException
    {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode)
                .build();
//        AuthorizationSingleton.getInstance(authorizationCodeRequest);
        try
        {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            System.out.println(spotifyApi.getAccessToken() + " : " + spotifyApi.getRefreshToken());
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        return spotifyApi.getAccessToken();
    }
}
