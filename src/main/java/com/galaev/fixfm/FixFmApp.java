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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class FixFmApp extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(FixFmApp.class);
    private String artist;
    private String album;
    private String track;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //String[] parameters = req.getQueryString().split("&");

        fix();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet getTracks = new HttpGet("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=antongalaev&api_key=599a9514090a65b21d9c7d0e47605090&format=json");
        CloseableHttpResponse tracksResponse = httpClient.execute(getTracks);
        HttpEntity entity = tracksResponse.getEntity();
        JsonReader jsonReader =  Json.createReader(entity.getContent());
        String name = jsonReader.readObject().getJsonObject("toptracks").getJsonArray("track").getJsonObject(0).getString("name");


        PrintWriter responseWriter = resp.getWriter();
        responseWriter.print("<p>Done.</p> <p>Wrong scrobbles are gone.</p> " + name);
        responseWriter.close();
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
