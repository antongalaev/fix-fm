package com.galaev.fixfm.model;

/**
 * This class represents a Last.fm user.
 *
 * @author Anton Galaev
 */
public class User {

    // user login
    private String login;
    // user token
    private String token;
    // user session key
    private String sessionKey;

    // Getters and setters

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
