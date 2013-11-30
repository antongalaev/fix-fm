package com.galaev.fixfm;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 27/11/2013
 * Time: 04:04
 */
public class FixFmApp {

    private static Logger logger = LoggerFactory.getLogger(FixFmServlet.class);

    // API constants
    private static final String API_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String API_KEY = "599a9514090a65b21d9c7d0e47605090";
    private static final String API_SECRET = "e5a8f63bcabd274723439d8ca89b872a";

    // Responses
    private static final String FIX_DONE = "<p>Done.</p> <p>Wrong scrobbles are gone.</p> ";

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String login;
    private String artist;
    private String album;
    private String oldTag;
    private String newTag;
    private String token;
    private String sessionKey;
    private int playcount;

    public String process() throws IOException {
        // user authentication
        authenticate();
        // find out track playcount
        findTrackPlaycount();


        return FIX_DONE;
    }

    public void authenticate() throws IOException {
        String api_sig = signApiGetCall("auth.getSession");
        String authURL = new StringBuilder()
                .append(API_ROOT_URL)
                .append("?method=auth.getSession&api_key=")
                .append(API_KEY)
                .append("&api_sig=")
                .append(api_sig)
                .append("&token=")
                .append(token)
                .append("&format=json")
                .toString();
        HttpGet authRequest = new HttpGet(authURL);
        try (CloseableHttpResponse authResponse = httpClient.execute(authRequest)) {
            HttpEntity entity = authResponse.getEntity();
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            sessionKey = jsonResponse.getJsonObject("session").getString("key");
            String sessionLogin = jsonResponse.getJsonObject("session").getString("name");
            logger.info("Logged as " + sessionLogin);
        }
    }

    private void fix() throws IOException {
        removeTrack();
        scrobbleTrack();
    }

    private void removeTrack() throws IOException {
        // create request and its entity form (alphabetically sorted!!)
        HttpPost removeRequest = new HttpPost(API_ROOT_URL);
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("api_key", API_KEY));
        form.add(new BasicNameValuePair("artist", artist));

        form.add(new BasicNameValuePair("sk", sessionKey));
        form.add(new BasicNameValuePair("track", oldTag));
        form.add(new BasicNameValuePair("api_sig", signApiPostCall(form)));
        // set request entity
        try {
            removeRequest.setEntity(new UrlEncodedFormEntity(form));
        } catch (UnsupportedEncodingException e) {
            logger.error("Error with url-encoded form removing track");
            e.printStackTrace();
        }
        // execute request
        try (CloseableHttpResponse authResponse = httpClient.execute(removeRequest)) {
            HttpEntity entity = authResponse.getEntity();
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            sessionKey = jsonResponse.getJsonObject("sessionKey").getString("key");
            String sessionLogin = jsonResponse.getJsonObject("sessionKey").getString("name");
            logger.info("Logged as " + sessionLogin);
        }
    }

    private void scrobbleTrack() throws IOException {
        // create request and its entity (form)
        HttpPost removeRequest = new HttpPost(API_ROOT_URL);
        List<NameValuePair> form = new ArrayList<>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000 - 10000);


        form.add(new BasicNameValuePair("album", album));
        form.add(new BasicNameValuePair("artist", artist));
        timestamp += 300;
        form.add(new BasicNameValuePair("timestamp", timestamp));
        form.add(new BasicNameValuePair("track", newTag));



        form.add(new BasicNameValuePair("api_key", API_KEY));
        form.add(new BasicNameValuePair("method", "track.scrobble"));
        form.add(new BasicNameValuePair("sk", sessionKey));



        form.add(new BasicNameValuePair("api_sig", signApiPostCall(form)));
        // set request entity
        try {
            removeRequest.setEntity(new UrlEncodedFormEntity(form));
        } catch (UnsupportedEncodingException e) {
            logger.error("Error with url-encoded form removing track");
            e.printStackTrace();
        }
        // execute request
        try (CloseableHttpResponse authResponse = httpClient.execute(removeRequest)) {
            HttpEntity entity = authResponse.getEntity();
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            sessionKey = jsonResponse.getJsonObject("sessionKey").getString("key");
            String sessionLogin = jsonResponse.getJsonObject("sessionKey").getString("name");
            logger.info("Logged as " + sessionLogin);
        }
    }


    private void findTrackPlaycount() throws IOException {
        String authURL = new StringBuilder()
                .append(API_ROOT_URL)
                .append("?method=track.getInfo&api_key=")
                .append(API_KEY)
                .append("&artist=")
                .append(artist)
                .append("&track=")
                .append(oldTag)
                .append("&username=")
                .append(login)
                .append("&format=json")
                .toString();
        HttpGet authRequest = new HttpGet(authURL);
        try (CloseableHttpResponse authResponse = httpClient.execute(authRequest)) {
            HttpEntity entity = authResponse.getEntity();
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            playcount = Integer.parseInt(jsonResponse.getJsonObject("track").getString("userplaycount"));
            logger.info("Userplaycount extracted: " + playcount);
        }
    }

    private String signApiGetCall(String method) {
        String signed = new StringBuilder()
                .append("api_key")
                .append(API_KEY)
                .append("method")
                .append(method)
                .append("token")
                .append(token)
                .append(API_SECRET)
                .toString();
        return DigestUtils.md5Hex(signed);
    }

    private String signApiPostCall(List<NameValuePair> form) {
        StringBuilder signed = new StringBuilder();
        for (NameValuePair pair : form) {
            signed.append(pair.getName()).append(pair.getValue());
        }
        signed.append(API_SECRET);
        return DigestUtils.md5Hex(signed.toString());
    }

    public void extractParams(HttpServletRequest request) {
        login = request.getParameter("login");
        artist = request.getParameter("artist");
        album = request.getParameter("album");
        oldTag = request.getParameter("oldtag");
        newTag = request.getParameter("newtag");
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getOldTag() {
        return oldTag;
    }

    public String getNewTag() {
        return newTag;
    }
}
