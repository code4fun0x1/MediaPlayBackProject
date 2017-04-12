package com.example.shashank.mediaplaybackproject.model;

import java.util.ArrayList;

/**
 * Created by Shashank on 11-04-2017.
 */

public class FetchSong {

    String cover;
    String nameAlbum;
    String nameArtist;
    ArrayList<IndividualFetchSong> songs = new ArrayList<>();
    String url="";
    String playCount="";


    public FetchSong() {

    }

    public FetchSong(String playCount,String url,String cover, String nameAlbum, String nameArtist, ArrayList<IndividualFetchSong> songs) {
        this.cover = cover;
        this.url=url;
        this.playCount=playCount;
        this.nameAlbum = nameAlbum;
        this.nameArtist = nameArtist;
        this.songs = songs;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public String getNameArtist() {
        return nameArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public ArrayList<IndividualFetchSong> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<IndividualFetchSong> songs) {
        this.songs = songs;
    }


}
