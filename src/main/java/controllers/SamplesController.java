package main.java.controllers;

import javafx.util.Pair;
import main.java.services.SamplesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SamplesController
{
    private final SamplesService mSampleService = new SamplesService();

    @GetMapping(value = "/song-samples")
    public Pair<String, String> forkPlaylist(@RequestParam("id") String songId)
    {
        return mSampleService.getSongSamples(songId);
    }

}
