package main.java.controllers;

import main.java.services.PlaylistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;

import java.util.List;

@RestController
public class PlaylistController
{
    private final PlaylistService playlistService = new PlaylistService();

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
    public <T extends AbstractModelObject> List<T> searchPlaylist(@RequestParam("search") String search, @RequestParam("type") String type)
    {
       return playlistService.search(search, type);
    }


}
