package main.java.models;

import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
// TODO - remove class if not used
public class AuthorizationSingleton {
    private static AuthorizationSingleton mAuthorizationSingleton = null;
    public AuthorizationCodeRequest mAuthorizationCodeRequest;


    private AuthorizationSingleton(AuthorizationCodeRequest authorizationCodeRequest)
    {
        mAuthorizationCodeRequest = authorizationCodeRequest;
    }

    public static AuthorizationSingleton getInstance(AuthorizationCodeRequest authorizationCodeRequest)
    {
        if (mAuthorizationSingleton == null)
            mAuthorizationSingleton = new AuthorizationSingleton(authorizationCodeRequest);
        return mAuthorizationSingleton;
    }
}
