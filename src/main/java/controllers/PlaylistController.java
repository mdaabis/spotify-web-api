package main.java.controllers;

import main.java.services.PlaylistService;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class PlaylistController
{
    private final PlaylistService playlistService = new PlaylistService();

    @GetMapping(value = "/user-playlists")
    public List<String> userPlaylists()
    {
        return playlistService.getUserPlaylists();
    }
}
