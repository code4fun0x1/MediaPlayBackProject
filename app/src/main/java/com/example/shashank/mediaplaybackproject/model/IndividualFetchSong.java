package com.example.shashank.mediaplaybackproject.model;

/**
 * Created by Shashank on 12-04-2017.
 */

public class IndividualFetchSong  {
    String songName;
    String duration;

    public IndividualFetchSong() {
    }



    public IndividualFetchSong(String songName, String duration) {
        this.songName = songName;
        this.duration = duration;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}