package com.galaev.fixfm;

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

    private static final String API_KEY = "599a9514090a65b21d9c7d0e47605090";
    private static final String API_SECRET = "e5a8f63bcabd274723439d8ca89b872a";

    private String login;
    private String artist;
    private String album;
    private String oldTag;
    private String newTag;

    public void process(HttpServletRequest request) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet getTracks = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=antongalaev&api_key=599a9514090a65b21d9c7d0e47605090&format=json");
        CloseableHttpResponse tracksResponse = httpClient.execute(getTracks);
        HttpEntity entity = tracksResponse.getEntity();
        JsonReader jsonReader =  Json.createReader(entity.getContent());
        String name = jsonReader.readObject().getJsonObject("toptracks").getJsonArray("track").getJsonObject(0).getString("name");


    }

    private void extractParams(HttpServletRequest request) {
        login = request.getParameter("login");
        artist = request.getParameter("artist");
        album = request.getParameter("album");
        oldTag = request.getParameter("oldtag");
        newTag = request.getParameter("newtag");
    };

    private void authenticate() {

    }

    private void fix() {
        removeTrack();
        scrobbleTrack();
    }


    private void removeTrack() {

    }

    private void scrobbleTrack() {

    }
}
