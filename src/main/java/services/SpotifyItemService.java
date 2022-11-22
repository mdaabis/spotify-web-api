package main.java.services;

import main.java.models.CombinePlaylistsRequest;
import main.java.utils.AuthorisationUtils;
import main.java.utils.SpotifyItemUtils;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.services.AuthorizationService.spotifyApi;

public class SpotifyItemService
{
    private final String REQUEST_URL = SpotifyItemUtils.BASE_SPOTIFY_URL + "playlists/";

    public List<String> getUserPlaylists()
    {
        GetListOfUsersPlaylistsRequest list = spotifyApi.getListOfUsersPlaylists(AuthorisationUtils.USER_ID)
                .build();
        try
        {
            final Paging<PlaylistSimplified> playlistPaging = list.execute();
            return Arrays.stream(playlistPaging.getItems()).toList().stream().map(this::formatPlaylists).collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String createNewPlaylist(String name)
    {
        String playlistId = null;
        CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(AuthorisationUtils.USER_ID, name)
                .collaborative(false)
                .public_(false)
                .build();
        try {
            final Playlist playlist = createPlaylistRequest.execute();
            playlistId = playlist.getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return playlistId;
    }

    public String[] forkPlaylist(String playlistIdToFork, String newPlaylistName)
    {
        String newPlaylistID = createNewPlaylist(newPlaylistName);

        String jsonString = getPlaylistByID(playlistIdToFork);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        }catch (JSONException e){
            System.out.println("Error" + e.toString());
        }

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        List<String> trackUriList = IntStream
                .range(0, jsonArray.length())
                .mapToObj(i -> returnTrackUri(jsonArray.getJSONObject(i))).toList();
        String[] trackUriArray = trackUriList.toArray(new String[trackUriList.size()]);
        addTracksToPlaylist(newPlaylistID, trackUriArray);

        return trackUriArray;
    }

    public Set<String> combineAndForkMultiplePlaylists(CombinePlaylistsRequest combinePlaylistsRequest)
    {
        String[] playlistIdsToForkArray = combinePlaylistsRequest.getPlaylistIds().split(",");
        String newPlaylistName = combinePlaylistsRequest.getNewPlaylistName();
        String newPlaylistID = createNewPlaylist(newPlaylistName);

        Set<String> trackUriSet = new HashSet<>();

        for (String playlistId : playlistIdsToForkArray)
        {
            String jsonString = getPlaylistByID(playlistId);
            JSONObject jsonObject = null;
            try
            {
                jsonObject = new JSONObject(jsonString);
            }
            catch (JSONException e)
            {
                System.out.println("Error" + e.toString());
            }

            JSONArray jsonArray = jsonObject != null ? jsonObject.getJSONArray("items") : null;

            trackUriSet = jsonArray != null ? IntStream
                    .range(0, jsonArray.length())
                    .mapToObj(i -> returnTrackUri(jsonArray.getJSONObject(i))).collect(Collectors.toSet()) : new HashSet<>();
            
            String[] trackUriArray = trackUriSet.toArray(new String[0]);
            addTracksToPlaylist(newPlaylistID, trackUriArray);
        }
        return trackUriSet;
    }

    public <T extends AbstractModelObject> List<T> search(String searchString, String... typeArray)
    {
        String type = String.join(",", typeArray);
        final SearchItemRequest searchItemRequest = spotifyApi.searchItem(searchString, type)
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

    public void addTracksToPlaylist(String playlistId, String[] uris)
    {
        final AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .build();
        try {
            final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getPlaylistByID(String playlistId)
    {
        StringBuilder result = new StringBuilder();
        String getPlaylistItemsUrl = REQUEST_URL + playlistId + "/tracks";
        try
        {
            URL url = new URL(getPlaylistItemsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + spotifyApi.getAccessToken());
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

    private String returnTrackUri(JSONObject json)
    {
        return json.getJSONObject("track").getString("uri");
    }

    private String formatPlaylists(PlaylistSimplified playlist)
    {
        return playlist.getName() + " : " + playlist.getId();
    }
}
