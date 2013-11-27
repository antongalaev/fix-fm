package com.galaev.fixfm;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 27/11/2013
 * Time: 04:04
 */
public class FixFmApp {

    private static Logger logger = LoggerFactory.getLogger(FixFmServlet.class);

    private static final String API_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String API_KEY = "599a9514090a65b21d9c7d0e47605090";
    private static final String API_SECRET = "e5a8f63bcabd274723439d8ca89b872a";
    private static final String FIX_DONE = "<p>Done.</p> <p>Wrong scrobbles are gone.</p> ";

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String api_sig;
    private String login;
    private String artist;
    private String album;
    private String oldTag;
    private String newTag;
    private String token;

    public void extractParams(HttpServletRequest request) {
        login = request.getParameter("login");
        artist = request.getParameter("artist");
        album = request.getParameter("album");
        oldTag = request.getParameter("oldtag");
        newTag = request.getParameter("newtag");
    }

    public String process() throws IOException {
        authenticate();
        HttpGet getTracks = new HttpGet(API_ROOT_URL + "?method=user.gettoptracks&user=antongalaev&api_key=" + API_KEY +"&format=json");
        CloseableHttpResponse tracksResponse = httpClient.execute(getTracks);
        HttpEntity entity = tracksResponse.getEntity();
        JsonReader jsonReader =  Json.createReader(entity.getContent());
        String name = jsonReader.readObject().getJsonObject("toptracks").getJsonArray("track").getJsonObject(0).getString("name");

        return FIX_DONE;
    }

    private void authenticate() throws IOException {
        api_sig = signApiCall("auth.getSession");
        String authURL = new StringBuilder()
                .append(API_ROOT_URL)
                .append("?method=auth.getsession&api_key=")
                .append(API_KEY)
                .append("&api_sig=")
                .append(api_sig)
                .append("&token=")
                .append(token)
                .append("&format=json")
                .toString();
        HttpGet authRequest = new HttpGet(authURL);
        CloseableHttpResponse authResponse = httpClient.execute(authRequest);
        HttpEntity entity = authResponse.getEntity();
    }

    private String signApiCall(String method) {
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

    private void fix() {
        removeTrack();
        scrobbleTrack();
    }


    private void removeTrack() {

    }

    private void scrobbleTrack() {

    }

    public void setToken(String token) {
        this.token = token;
    }
}
