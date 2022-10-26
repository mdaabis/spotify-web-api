package main.java.services;

import main.java.utils.AuthorisationUtils;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistService
{
    public List<String> getUserPlaylists()
    {
        GetListOfUsersPlaylistsRequest list = AuthorizationService.spotifyApi.getListOfUsersPlaylists(AuthorisationUtils.USER_ID)
                .build();
        try
        {
            final Paging<PlaylistSimplified> playlistPaging = list.execute();
            return Arrays.stream(playlistPaging.getItems()).toList().stream().map(el -> playlists(el)).collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String playlists(PlaylistSimplified playlist)
    {
        return playlist.getName() + " : " + playlist.getId();
    }

    private void getTokensFromSingleton()
    {

    }
}
