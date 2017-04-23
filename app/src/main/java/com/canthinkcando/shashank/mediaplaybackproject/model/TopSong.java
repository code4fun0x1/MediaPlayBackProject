package com.canthinkcando.shashank.mediaplaybackproject.model;

/**
 * Created by Shashank on 12-04-2017.
 */

public class TopSong {

    private String title;
    private String mbid;
    private String cover;
    private String url;

    public TopSong() {
    }

    public TopSong(String title, String mbid, String cover, String url) {
        this.title = title;
        this.mbid = mbid;
        this.cover = cover;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
