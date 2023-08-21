package main.java.models;

import javafx.util.Pair;

import java.util.List;

public class Song
{
    // Todo check if these can be final
    private String name;
    private String artist;
    private String whosampledPage;
    private String image;

    private List<Pair<String, String>> samples;
    private List<Pair<String, String>> sampledIn;
    private List<Pair<String, String>> covers;
    private List<Pair<String, String>> remixes;

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

    public List<Pair<String, String>> getSamples() {
        return samples;
    }

    public void setSamples(List<Pair<String, String>> samples) {
        this.samples = samples;
    }

    public List<Pair<String, String>> getSampledIn() {
        return sampledIn;
    }

    public void setSampledIn(List<Pair<String, String>> sampledIn) {
        this.sampledIn = sampledIn;
    }

    public List<Pair<String, String>> getCovers() {
        return covers;
    }

    public void setCovers(List<Pair<String, String>> covers) {
        this.covers = covers;
    }

    public List<Pair<String, String>> getRemixes() {
        return remixes;
    }

    public void setRemixes(List<Pair<String, String>> remixes) {
        this.remixes = remixes;
    }
}
