package main.java.services;

import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

public class SamplesService
{
    private final SpotifyItemService spotifyItemService = new SpotifyItemService();
    private static final String WHOSAMPLED_URL = "https://www.whosampled.com/search/tracks/?q=";

    public Pair<String, String> getSongSamples(String songId)
    {
            Pair<String, String> trackNameAndArtist = spotifyItemService.getTrackNameAndArtistById(songId);

            String webpageHtml = getWebpage();

        return trackNameAndArtist;
    }

    public String getWebpage()
    {
        StringBuilder html = new StringBuilder(0);
        try
        {
            String val;
            URL url = new URL(WHOSAMPLED_URL + "silence");
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla");

            // Reading the HTML content from the .HTML File
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            /* Catching the string and  if found any null
             character break the String */
            while ((val = br.readLine()) != null)
            {
                html.append(val+"\n");
            }
            br.close();

            Connection connection = Jsoup.connect(WHOSAMPLED_URL+"silence");
            connection.userAgent("Mozilla");
            Document doc = connection.get();
            Elements listOfSongs = doc.getElementsByClass("list bordered-list");
            String el = "";
            for (Element element : listOfSongs.first().children())
            {
               if (element.toString().toLowerCase().contains("khalid"))
               {
                   el = element.toString();
               }
            }
            System.out.println("element: " + el);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return html.toString();
    }

    public void parseHtml(String html)
    {

    }
}
