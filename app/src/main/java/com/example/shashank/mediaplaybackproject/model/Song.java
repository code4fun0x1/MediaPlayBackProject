package com.example.shashank.mediaplaybackproject.model;

import android.graphics.Bitmap;

/**
 * Created by Shashank on 18-10-2016.
 */

public class Song {
    private String DATA;
    private String TITLE;
    private  String ID;
    private String ALBUM;
    private String ARTIST;
    private Bitmap ALBUMART;
    private String artPath;

    public Song(String DATA,String ALBUM, Bitmap ALBUMART, String ID, String ARTIST, String TITLE,String artPath) {
        this.DATA=DATA;
        this.ALBUM = ALBUM;
        this.ALBUMART = ALBUMART;
        this.ID = ID;
        this.ARTIST = ARTIST;
        this.TITLE = TITLE;
        this.artPath=artPath;
    }


    public String getALBUM() {
        return ALBUM;
    }

    public void setALBUM(String ALBUM) {
        this.ALBUM = ALBUM;
    }

    public Bitmap getALBUMART() {
        return ALBUMART;
    }

    public void setALBUMART(Bitmap ALBUMART) {
        this.ALBUMART = ALBUMART;
    }

    public String getARTIST() {
        return ARTIST;
    }

    public void setARTIST(String ARTIST) {
        this.ARTIST = ARTIST;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }
}
