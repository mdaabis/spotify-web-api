package main.java.services;

import javafx.util.Pair;
import main.java.models.Song;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SamplesService
{
    private final SpotifyItemService spotifyItemService = new SpotifyItemService();
    private static final String WHOSAMPLED_URL = "https://www.whosampled.com/search/tracks/?q=";

    public Pair<String, List<String>> getRelatedSongs(String songId)
    {
            Pair<String, List<String>> trackNameAndArtist = spotifyItemService.getTrackNameAndArtistById(songId);

            Element trackLinkElement = getTrackLinkFromWebpage(trackNameAndArtist.getKey(), trackNameAndArtist.getValue());

            Document songWebpage = getSongWebpage(trackLinkElement);

            createSongObject(songWebpage);

        return trackNameAndArtist;
    }

    protected Element getTrackLinkFromWebpage(String songName, List<String> artists)
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
                if (elementStr.contains(songName.toLowerCase()) && anyArtistCredited(artists, elementStr))
                {
                    songListEntry = element;
                    break;
                    //TODO currently only dealing with first case of title matching. Need to handle multiple
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return songListEntry;
    }

    protected Document getSongWebpage(Element element)
    {
        String linkAddress = Objects.requireNonNull(element.select("li.listEntry.trackEntry > a").first()).attr("abs:href"); // Gets absolute path rather than relative
        try
        {
            return Jsoup.connect(linkAddress)
                    .userAgent("Mozilla")
                    .get();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected Song createSongObject(Document webpage)
    {
        String[] nameAndArtist = webpage.getElementsByTag("h1").text().split(" by ");
        String name = nameAndArtist[0];
        String artist = nameAndArtist[nameAndArtist.length - 1];
        String url = webpage.location();

        Elements itemPropElements = webpage.getElementsByAttribute("itemprop");
        Element imageElement = itemPropElements.stream()
                        .filter(e -> e.toString().contains("thumbnailUrl")).toList().get(0);
        String imageUrl = imageElement.attr("abs:src");

        Song song = new Song(name, artist, url, imageUrl);

        Elements x = webpage.getElementsByClass("sampleEntry");

        return new Song();
    }

    protected boolean anyArtistCredited(List<String> artists, String elementStr)
    {
        for(String artist : artists)
        {
            if(elementStr.contains(artist.toLowerCase()))
                return true;
        }
        return false;
    }
}
