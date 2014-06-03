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

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is intended to work with Last.fm API methods.
 *
 * @author Anton Galaev
 */
public class LastFmApi {

    private static Logger logger = Logger.getLogger(String.valueOf(LastFmApi.class));

    // API constants
    private static final String API_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
    private static final String API_KEY = "599a9514090a65b21d9c7d0e47605090";
    private static final String API_SECRET = "e5a8f63bcabd274723439d8ca89b872a";

    // API calls
    private static final String API_CALL_AUTH = API_ROOT_URL + "?method=auth.getSession&api_key=" + API_KEY +
            "&api_sig=%s&token=%s&format=json";
    private static final String API_CALL_FIND_PLAYCOUNT = API_ROOT_URL + "?method=track.getInfo&api_key=" +API_KEY +
            "&artist=%s&track=%s&username=%s&format=json";

    // http client
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    // user, that wants to fix tags
    private User user;

    /**
     * Public constructor, that creates
     * new Api object to work with the provided user.
     *
     * @param user user, that wants to fix tags
     */
    public LastFmApi(User user) {
        this.user = user;
    }

    /**
     * Gets user session key from Last.fm api.
     * Sets the session key field in the {@code User} object here.
     *
     * @return session key
     * @throws IOException
     */
    public String setUserSessionKey() throws IOException {
        logger.info("Trying to get user session key.");
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

    /**
     * Gets track playcount from Last.fm Api.
     * Sets the corresponding field in the provided {@code Track} object.
     *
     * @param track track to find playcount for
     * @return playcount
     * @throws IOException
     */
    public int setTrackPlaycount(Track track) throws IOException {
        logger.info("Trying to get playcount.");
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

    /**
     * Removes track from user library.
     *
     * @param track track to remove
     * @throws IOException
     */
    public void removeTrack(Track track) throws IOException {
        logger.info("Trying to remove track from library.");
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

    /**
     * Scrobbles track to user library.
     *
     * @param track track to scrobble
     * @throws IOException
     */
    public void scrobbleTrack(Track track) throws IOException {
        logger.info("Trying to scrobble track to the library.");
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

    /**
     * Signs api GET call following Last.fm API rules.
     *
     * @param method api method to call
     * @return signed string
     */
    private String signApiGetCall(String method) {
        String signed = "api_key" + API_KEY + "method" + method + "token" + user.getToken() + API_SECRET;
        return DigestUtils.md5Hex(signed);
    }

    /**
     * Signs api POST call following Last.fm API rules.
     *
     * @param form http post form
     * @return signed string
     */
    private String signApiPostCall(List<NameValuePair> form) {
        StringBuilder signed = new StringBuilder();
        for (NameValuePair pair : form) {
            signed.append(pair.getName()).append(pair.getValue());
        }
        signed.append(API_SECRET);
        return DigestUtils.md5Hex(signed.toString());
    }

    /**
     * Urlencode a string in utf-8 encoding.
     * It is used to urlencode GET method parameters.
     *
     * @param value string to encode
     * @return utf-8 encoded string
     * @throws UnsupportedEncodingException
     */
    private String urlencode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
