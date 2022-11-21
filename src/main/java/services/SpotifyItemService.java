package main.java.services;

import main.java.utils.AuthorisationUtils;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpotifyItemService
{
    private final String REQUEST_URL = AuthorisationUtils.BASE_SPOTIFY_URL + "playlists/";

    public List<String> getUserPlaylists()
    {
        GetListOfUsersPlaylistsRequest list = AuthorizationService.spotifyApi.getListOfUsersPlaylists(AuthorisationUtils.USER_ID)
                .build();
        try
        {
            final Paging<PlaylistSimplified> playlistPaging = list.execute();
            return Arrays.stream(playlistPaging.getItems()).toList().stream().map(this::formatPlaylists).collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatPlaylists(PlaylistSimplified playlist)
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

    public void forkPlaylist(String id)
    {

    }

    public String getPlaylistByID(String id)
    {
        StringBuilder result = new StringBuilder();
        String getPlaylistItemsUrl = REQUEST_URL + id + "/tracks";
        try
        {
            URL url = new URL(getPlaylistItemsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + AuthorizationService.spotifyApi.getAccessToken());
            conn.setRequestProperty("Content_Type", "application/json");
            conn.setRequestMethod("GET");

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
        }
        return result.toString();
    }

    public <T extends AbstractModelObject> List<T> search(String searchString, String... typeArray)
    {
        String type = String.join(",", typeArray);
        final SearchItemRequest searchItemRequest = AuthorizationService.spotifyApi.searchItem(searchString, type)
                .limit(50)
                .build();

        List<T> list = new ArrayList<>();
        try
        {
            final SearchResult searchResult = searchItemRequest.execute();

            switch (type)
            {
                case "playlist" -> Arrays.stream(searchResult.getPlaylists().getItems()).forEach(e -> list.add((T) e));
                case "track" -> Arrays.stream(searchResult.getTracks().getItems()).forEach(e -> list.add((T) e));
                case "album" -> Arrays.stream(searchResult.getAlbums().getItems()).forEach(e -> list.add((T) e));
                case "artist" -> Arrays.stream(searchResult.getArtists().getItems()).forEach(e -> list.add((T) e));
                case "show" -> Arrays.stream(searchResult.getShows().getItems()).forEach(e -> list.add((T) e));
                case "episode" -> Arrays.stream(searchResult.getEpisodes().getItems()).forEach(e -> list.add((T) e));
            }
        }
        catch (IOException | SpotifyWebApiException | ParseException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    public void addTracksToPlaylist()
    {

    }
}
