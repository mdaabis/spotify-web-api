package main.java.models;

import java.util.List;

public class CombinePlaylistsRequest
{
    private List<String> mPlaylistIds;
    private String mNewPlaylistName;

    public List<String> getPlaylistIds()
    {
        return mPlaylistIds;
    }

    public String getNewPlaylistName()
    {
        return mNewPlaylistName;
    }
}
