package main.java.models;

import java.net.URL;
import java.util.List;

public class Song
{
    // Todo check if these can be final
    private String name;
    private String artist;
    private String whosampledPage;
    private String image;

    private List<Song> samples;
    private List<Song> sampledIn;
    private List<Song> covers;
    private List<Song> remixes;

    public Song(String name, String artist, String whosampledPage, String image)
    {
        this.name = name;
        this.artist = artist;
        this.whosampledPage = whosampledPage;
        this.image = image;
    }

    public Song() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getWhosampledPage() {
        return whosampledPage;
    }

    public void setWhosampledPage(String whosampledPage) {
        this.whosampledPage = whosampledPage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Song> getSamples() {
        return samples;
    }

    public void setSamples(List<Song> samples) {
        this.samples = samples;
    }

    public List<Song> getSampledIn() {
        return sampledIn;
    }

    public void setSampledIn(List<Song> sampledIn) {
        this.sampledIn = sampledIn;
    }

    public List<Song> getCovers() {
        return covers;
    }

    public void setCovers(List<Song> covers) {
        this.covers = covers;
    }

    public List<Song> getRemixes() {
        return remixes;
    }

    public void setRemixes(List<Song> remixes) {
        this.remixes = remixes;
    }
}
