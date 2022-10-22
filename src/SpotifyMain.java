import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class SpotifyMain {

	private static final String CLIENT_ID = "e67295937de0463bba36cf5f6c8b8e6f";
	private static final String CLIENT_SECRET = "e8fb8960573142819dbec0d5c1763a5c";
	private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080");


	  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			    .setClientId(CLIENT_ID)
			    .setClientSecret(CLIENT_SECRET)
			    .setRedirectUri(redirectUri)
			    .build();
	  
	  private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
//			          .state("x4xkmn9pu3j6ukrs8n")
//			          .scope("user-read-birthdate,user-read-email")
//			          .show_dialog(true)
	    .build();

	  public static void authorizationCodeUri_Sync() {
	    final URI uri = authorizationCodeUriRequest.execute();

	    System.out.println("URI: " + uri.toString());
	  }

	  public static void authorizationCodeUri_Async() {
	    try {
	      final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

	      // Thread free to do other tasks...

	      // Example Only. Never block in production code.
	      final URI uri = uriFuture.join();

	      System.out.println("URI: " + uri.toString());
	    } catch (CompletionException e) {
	      System.out.println("Error: " + e.getCause().getMessage());
	    } catch (CancellationException e) {
	      System.out.println("Async operation cancelled.");
	    }
	  }

	  public static void main(String[] args) {
	    authorizationCodeUri_Sync();
	    authorizationCodeUri_Async();
	  }

}
