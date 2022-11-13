package main.java.services;

import main.java.utils.AuthorisationUtils;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
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
            return Arrays.stream(playlistPaging.getItems()).toList().stream().map(this::playlists).collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String playlists(PlaylistSimplified playlist)
    {
        return playlist.getName() + " : " + playlist.getId();
    }

    public void createNewPlaylist(String name)
    {
        CreatePlaylistRequest createPlaylistRequest = AuthorizationService.spotifyApi.createPlaylist(AuthorisationUtils.USER_ID, name)
                .collaborative(false)
                .public_(false)
                .description("MD testing api.")
                .build();

        try {
            final Playlist playlist = createPlaylistRequest.execute();
            System.out.println("Name: " + playlist.getName());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
