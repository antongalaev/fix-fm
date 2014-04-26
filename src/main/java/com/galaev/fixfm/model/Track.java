package com.galaev.fixfm.model;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 24/04/2014
 * Time: 19:33
 */
public class Track {

    private String artist;
    private String album;
    private String oldTag;
    private String newTag;
    private int playcount;

    public void populateWithPostData(HttpServletRequest request) {
        artist = request.getParameter("artist");
        album = request.getParameter("album");
        oldTag = request.getParameter("old");
        newTag = request.getParameter("new");
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }

    public String getOldTag() {
        return oldTag;
    }

    public void setOldTag(String oldTag) {
        this.oldTag = oldTag;
    }
}
