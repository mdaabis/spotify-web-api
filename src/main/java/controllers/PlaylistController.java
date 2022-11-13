package main.java.controllers;

import main.java.services.PlaylistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(value = "/new-playlist")
    public void createPlaylists(@RequestParam("name") String name)
    {
        playlistService.createNewPlaylist(name);
    }


}
