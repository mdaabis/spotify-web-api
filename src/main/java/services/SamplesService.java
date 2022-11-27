package main.java.services;

import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;

public class SamplesService
{
    private final SpotifyItemService spotifyItemService = new SpotifyItemService();
    private static final String WHOSAMPLED_URL = "https://www.whosampled.com/search/tracks/?q=";

    public Pair<String, String> getSongSamples(String songId)
    {
            Pair<String, String> trackNameAndArtist = spotifyItemService.getTrackNameAndArtistById(songId);

            Element trackLinkElement = getTrackLinkFromWebpage(trackNameAndArtist.getKey(), trackNameAndArtist.getValue());

            Document songWebpage = getSongWebpage(trackLinkElement);

        return trackNameAndArtist;
    }

    public Element getTrackLinkFromWebpage(String songName, String artist)
    {
        Element songListEntry = null;
        try
        {
            Connection connection = Jsoup.connect(WHOSAMPLED_URL + songName.toLowerCase());
            connection.userAgent("Mozilla");
            Document doc = connection.get();

            Elements listOfSongs = doc.getElementsByClass("list bordered-list");

            for (Element element : listOfSongs.first().children())
            {
                String elementStr = element.toString().toLowerCase();
                if (elementStr.contains(songName.toLowerCase()) && elementStr.contains((artist.toLowerCase())))
                {
                    songListEntry = element;
                    break;
                    // TODO currently only dealing with first case of title matching. Need to handle multiple
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return songListEntry;
    }

    public Document getSongWebpage(Element element)
    {
        String linkAddress = element.select("li.listEntry.trackEntry > a").first().attr("abs:href"); // Gets absolute path rather than relative
        try
        {
            Document songWebpage = Jsoup.connect(linkAddress)
                    .userAgent("Mozilla")
                    .get();
            return songWebpage;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
