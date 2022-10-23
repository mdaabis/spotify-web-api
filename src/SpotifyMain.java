import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

public class SpotifyMain {

	  public static String spotifyLogin() {
		  final SpotifyApi spotifyApi = new SpotifyApi.Builder()
				  .setClientId(AuthorisationUtils.CLIENT_ID)
				  .setClientSecret(AuthorisationUtils.CLIENT_SECRET)
				  .setRedirectUri(AuthorisationUtils.redirectUri)
				  .build();

		  AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
//				  .state("x4xkmn9pu3j6ukrs8n")
//				  .scope("user-read-birthdate,user-read-email")
//			      .show_dialog(true)
				  .build();
		  final URI uri = authorizationCodeUriRequest.execute();
		  return uri.toString();
	  }

	  public static void main(String[] args) {
		  System.out.println("test: " + spotifyLogin());

	  }

}
