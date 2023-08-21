package main.java.models;

import javafx.util.Pair;

import java.util.List;

public class Song
{
    // Todo check if these can be final
    private String mName;
    private String mArtist;
    private String mWhosampledPage;
    private String mImageUrl;
    private List<Pair<String, String>> mSamples;
    private List<Pair<String, String>> mSampledIn;
    private List<Pair<String, String>> mCovers;
    private List<Pair<String, String>> mRemixes;


    public Song(String name, String artist, String whosampledPage, String imageUrl)
    {
        mName = name;
        mArtist = artist;
        mWhosampledPage = whosampledPage;
        mImageUrl = imageUrl;
    }

    public Song(
            String name,
            String artist,
            String whosampledPage,
            String imageUrl,
            List<Pair<String, String>> samples,
            List<Pair<String, String>> sampledIn,
            List<Pair<String, String>> covers,
            List<Pair<String, String>> remixes)
    {
        mName = name;
        mArtist = artist;
        mWhosampledPage = whosampledPage;
        mImageUrl = imageUrl;
        mSamples = samples;
        mSampledIn = sampledIn;
        mCovers = covers;
        mRemixes = remixes;
    }

    
}
