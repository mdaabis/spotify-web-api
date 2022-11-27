package main.java.services;

import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SamplesService
{
    private final SpotifyItemService spotifyItemService = new SpotifyItemService();
    private static final String WHOSAMPLED_URL = "https://www.whosampled.com/search/tracks/?q=";

    public Pair<String, String> getSongSamples(String songId)
    {
            Pair<String, String> trackNameAndArtist = spotifyItemService.getTrackNameAndArtistById(songId);

            Element trackLinkElement = getTrackLinkFromWebpage(trackNameAndArtist.getKey());

            getSampledSongsFromLink(trackLinkElement);

        return trackNameAndArtist;
    }

    public Element getTrackLinkFromWebpage(String songName)
    {
        Element songListEntry = new Element("");
        try
        {
            Connection connection = Jsoup.connect(WHOSAMPLED_URL + songName.toLowerCase());
            connection.userAgent("Mozilla");
            Document doc = connection.get();

            Elements listOfSongs = doc.getElementsByClass("list bordered-list");

            for (Element element : listOfSongs.first().children())
            {
               if (element.toString().toLowerCase().contains("khalid"))
                   songListEntry = element;
            }

            System.out.println("element: " + songListEntry.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return songListEntry;
    }

    public void getSampledSongsFromLink(Element element)
    {

    }

    public void parseHtml(String html)
    {

    }
}
