package com.galaev.fixfm;

import com.galaev.fixfm.model.Track;
import com.galaev.fixfm.model.User;
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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 25/04/2014
 * Time: 19:18
 */
public class LastFmApi {

    private static Logger logger = LoggerFactory.getLogger(LastFmApi.class);

    // API constants
    private static final String API_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String API_KEY = "599a9514090a65b21d9c7d0e47605090";
    private static final String API_SECRET = "e5a8f63bcabd274723439d8ca89b872a";

    // API calls
    private static final String API_CALL_AUTH = API_ROOT_URL + "?method=auth.getSession&api_key=" + API_KEY +
            "&api_sig=%s&token=%s&format=json";
    private static final String API_CALL_FIND_PLAYCOUNT = API_ROOT_URL + "?method=track.getInfo&api_key=" +API_KEY +
            "&artist=%s&track=%s&username=%s&format=json";


    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private User user;

    public LastFmApi(User user) {
        this.user = user;
    }

    public String setUserSessionKey() throws IOException {
        // sign api call
        String api_sig = signApiGetCall("auth.getSession");
        // construct url
        String authURL = String.format(API_CALL_AUTH, api_sig, user.getToken());
        // execute request
        try (CloseableHttpResponse response = httpClient.execute(new HttpGet(authURL))) {
            HttpEntity entity = response.getEntity();
            // extract session key
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            String sessionKey = jsonResponse.getJsonObject("session").getString("key");
            logger.info("Session key received: " + sessionKey);
            user.setSessionKey(sessionKey);
            return sessionKey;
        }
    }

    public int setTrackPlaycount(Track track) throws IOException {
        // construct url
        String url = String.format(API_CALL_FIND_PLAYCOUNT,
                urlencode(track.getArtist()), urlencode(track.getOldTag()), user.getLogin());
        // execute request
        try (CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            HttpEntity entity = response.getEntity();
            // extract playcount
            JsonObject jsonResponse = Json.createReader(entity.getContent()).readObject();
            int playcount = Integer.parseInt(jsonResponse.getJsonObject("track").getString("userplaycount"));
            logger.info("Userplaycount received: " + playcount);
            track.setPlaycount(playcount);
            return playcount;
        }
    }

    public void removeTrack(Track track) throws IOException {
        // create request and its entity form (alphabetically sorted here)
        HttpPost removeRequest = new HttpPost(API_ROOT_URL);
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("api_key", API_KEY));
        form.add(new BasicNameValuePair("artist", track.getArtist()));
//        form.add(new BasicNameValuePair("format", "json"));
        form.add(new BasicNameValuePair("method", "library.removeTrack"));
        form.add(new BasicNameValuePair("sk", user.getSessionKey()));
        form.add(new BasicNameValuePair("track", track.getOldTag()));
        form.add(new BasicNameValuePair("api_sig", signApiPostCall(form)));
        // set request entity
        removeRequest.setEntity(new UrlEncodedFormEntity(form));
        // execute request
        try (CloseableHttpResponse response = httpClient.execute(removeRequest)){
            HttpEntity entity = response.getEntity();
            logger.info("Removing tracks response: " + EntityUtils.toString(entity));
        }
    }

    public void scrobbleTrack(Track track) throws IOException {
        // create request and its entity (form)
        HttpPost scrobbleRequest = new HttpPost(API_ROOT_URL);
        List<NameValuePair> form = new ArrayList<>();
        long timestamp = System.currentTimeMillis() / 1000 - 10000;
        // add array of scrobbles
        for (int i = 0; i < track.getPlaycount(); ++ i) {
            form.add(new BasicNameValuePair("album[" + i + "]", track.getAlbum()));
            form.add(new BasicNameValuePair("artist[" + i + "]", track.getArtist()));
            timestamp += 300;
            form.add(new BasicNameValuePair("timestamp[" + i + "]", String.valueOf(timestamp)));
            form.add(new BasicNameValuePair("track[" + i + "]", track.getNewTag()));
        }
        // add single parameters
        form.add(new BasicNameValuePair("api_key", API_KEY));
//        form.add(new BasicNameValuePair("format", "json"));
        form.add(new BasicNameValuePair("method", "track.scrobble"));
        form.add(new BasicNameValuePair("sk", user.getSessionKey()));
        // sort parameters
        Collections.sort(form, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        // sign call
        form.add(new BasicNameValuePair("api_sig", signApiPostCall(form)));
        // set request entity
        scrobbleRequest.setEntity(new UrlEncodedFormEntity(form));
        // execute request
        try (CloseableHttpResponse response = httpClient.execute(scrobbleRequest)) {
            HttpEntity entity = response.getEntity();
            logger.info("Scrobbling tracks response: " + EntityUtils.toString(entity));
        }
    }

    private String signApiGetCall(String method) {
        String signed = "api_key" + API_KEY + "method" + method + "token" + user.getToken() + API_SECRET;
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

    private String urlencode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
