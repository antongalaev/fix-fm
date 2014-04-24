package com.galaev.fixfm.model;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 24/04/2014
 * Time: 19:33
 */
public class User {

    private String login;
    private String token;
    private String sessionKey;

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
