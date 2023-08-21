package main.java.controllers;

import main.java.models.CombinePlaylistsRequest;
import main.java.services.SpotifyItemService;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class SpotifyItemController
{
    private final SpotifyItemService playlistService = new SpotifyItemService();

    @GetMapping(value = "/user-playlists")
    public List<String> userPlaylists()
    {
        return playlistService.getUserPlaylists();
    }

    @GetMapping(value = "/new-playlist")
    public void createPlaylists(@RequestParam("name") String name)
    {
        playlistService.createNewPlaylist(name);
    }

    @GetMapping(value = "/search")
    public <T extends AbstractModelObject> Map<String, List<T>> searchPlaylist(@RequestParam("search") String search, @RequestParam("type") String type)
    {
       return playlistService.searchByType(search, type);
    }

    @GetMapping(value = "/get-playlist")
    public String getPlaylistById(@RequestParam("id") String id)
    {
        return playlistService.getPlaylistByID(id);
    }

    @GetMapping(value = "/fork-playlist")
    public String[] forkPlaylist(@RequestParam("id") String playlistId, @RequestParam("name") String newPlaylistName)
    {
        return playlistService.forkPlaylist(playlistId, newPlaylistName);
    }

    @PostMapping(value = "/combine-playlists")
    public Set<String> combinePlaylists(@RequestBody CombinePlaylistsRequest combinePlaylistsRequest)
    {
        return playlistService.combineAndForkMultiplePlaylists(combinePlaylistsRequest);
    }
}
