package main.java.services;

import main.java.utils.AuthorisationUtils;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

public class AuthorisationService {

    public static String spotifyLogin() {
        final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(AuthorisationUtils.CLIENT_ID)
                .setClientSecret(AuthorisationUtils.CLIENT_SECRET)
                .setRedirectUri(AuthorisationUtils.redirectUri)
                .build();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .build();
        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }
}
