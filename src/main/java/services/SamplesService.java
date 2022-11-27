package main.java.services;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static main.java.services.AuthorizationService.spotifyApi;

public class SamplesService
{
    private final SpotifyItemService spotifyItemService = new SpotifyItemService();
    private static final String WHOSAMPLED_URL = "https://www.whosampled.com/search/tracks/?q=";

    public Pair<String, String> getSongSamples(String songId)
    {
            Pair<String, String> trackNameAndArtist = spotifyItemService.getTrackNameAndArtistById(songId);

            getWebPage();

        return trackNameAndArtist;
    }

    public void getWebPage()
    {
        StringBuilder html = new StringBuilder(0);
        try {
            String val;
            URL url = new URL(WHOSAMPLED_URL + "silence");
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla");

            // Reading the HTML content from the .HTML File
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            /* Catching the string and  if found any null
             character break the String */
            while ((val = br.readLine()) != null) {
                html.append(val);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("html: " + html.toString());
    }
}
