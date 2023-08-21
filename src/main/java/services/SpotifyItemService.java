package main.java.services;

import javafx.util.Pair;
import main.java.models.CombinePlaylistsRequest;
import main.java.utils.AuthorisationUtils;
import main.java.utils.SpotifyItemUtils;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.*;
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
    private final Logger log = LoggerFactory.getLogger(SpotifyItemService.class);

    public List<String> getUserPlaylists()
    {
        GetListOfUsersPlaylistsRequest list = spotifyApi.getListOfUsersPlaylists(AuthorisationUtils.USER_ID)
                .build();
        try
        {
            final Paging<PlaylistSimplified> playlistPaging = list.execute();
            return Arrays.stream(playlistPaging.getItems()).toList().stream().map(this::formatPlaylists).collect(Collectors.toList());
        }
        catch (IOException | SpotifyWebApiException | ParseException e)
        {
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
        try
        {
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
        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            System.out.println("Error" + e); //TODO specify error
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
        List<String> playlistIdsToForkArray = combinePlaylistsRequest.getPlaylistIds();
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

    public <T extends AbstractModelObject> Map<String, List<T>> searchByType(String searchString, String types)
    {
        Map<String, List<T>> searchResultsMap = new HashMap<>();
        Arrays.stream(types.split(","))
                .forEach(type -> searchByType(searchResultsMap, searchString, type));

        searchResultsMap.values().forEach(list ->
        {
            list.sort((o1, o2) -> {
                if (o1 instanceof Artist && o2 instanceof Artist) {
                    return ((Artist) o2).getPopularity().compareTo(((Artist) o1).getPopularity());
                } else if (o1 instanceof Track && o2 instanceof Track) {
                    return ((Track) o2).getPopularity().compareTo(((Track) o1).getPopularity());
                } else {
                    return 0; // No sorting for other subclasses
                }
            });
        });

        return searchResultsMap;
    }

    private <T extends AbstractModelObject> void searchByType(Map<String, List<T>> searchResultsMap, String searchString, String type)
    {
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(searchString, type)
                .limit(50)
                .build();

        try
        {
            SearchResult searchResult = searchItemRequest.execute();

            switch (type)
            {
                case "playlist" -> Arrays.stream(searchResult.getPlaylists().getItems()).forEach(e ->   searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
                case "track" -> Arrays.stream(searchResult.getTracks().getItems()).forEach(e ->         searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
                case "album" -> Arrays.stream(searchResult.getAlbums().getItems()).forEach(e ->         searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
                case "artist" -> Arrays.stream(searchResult.getArtists().getItems()).forEach(e ->       searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
                case "show" -> Arrays.stream(searchResult.getShows().getItems()).forEach(e ->           searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
                case "episode" -> Arrays.stream(searchResult.getEpisodes().getItems()).forEach(e ->     searchResultsMap.computeIfAbsent(type, k -> new ArrayList<>()).add((T) e));
            }
        }
        catch (IOException | SpotifyWebApiException | ParseException e)
        {
            log.debug("Search for {} for type {} unsuccessful because of {}", searchString, type, e.getMessage());
        }
    }

    private void addTracksToPlaylist(String playlistId, String[] uris)
    {
        final AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .build();
        try
        {
            addItemsToPlaylistRequest.execute();
        }
        catch (IOException | SpotifyWebApiException | ParseException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getPlaylistByID(String playlistId)
    {
        StringBuilder result = new StringBuilder();
        String REQUEST_URL = SpotifyItemUtils.BASE_SPOTIFY_URL + "playlists/";
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
            while ((line = reader.readLine()) != null)
            {
                result.append(line);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
        }
        return result.toString();
    }

    public Pair<String, List<String>> getTrackNameAndArtistById(String songId)
    {
        Pair<String, List<String>> songNameAndArtist = null;
        try
        {
            Track track = spotifyApi.getTrack(songId)
                    .build()
                    .execute();

            List<String> artists = Arrays.stream(track.getArtists())
                    .map(ArtistSimplified::getName)
                    .collect(Collectors.toList());
            songNameAndArtist = new Pair<>(track.getName(), artists);
        }
        catch (IOException | SpotifyWebApiException | ParseException e) //TODO split up and add specific logs
        {
            throw new RuntimeException(e);
        }

        return songNameAndArtist;
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
