package com.galaev.fixfm.model;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a music track.
 *
 * @author Anton Galaev
 */
public class Track {

    // artist name
    private String artist;
    // album name
    private String album;
    // old song title
    private String oldTag;
    // new song title
    private String newTag;
    // song playcount
    private int playcount;

    /**
     * Fills track object with data from request parameters.
     *
     * @param request http request
     */
    public void populateWithPostData(HttpServletRequest request) {
        artist = request.getParameter("artist");
        album = request.getParameter("album");
        oldTag = request.getParameter("old");
        newTag = request.getParameter("new");
    }

    // Getters and setters

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
