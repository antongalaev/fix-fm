package com.galaev.fixfm;

import com.galaev.fixfm.dao.UserDao;
import com.galaev.fixfm.model.User;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class represents a servlet for authentication.
 * It expects "login" parameter in a post request.
 * It gets auth. token for the user either with authentication via Last.fm
 * or extracts it from the database.
 *
 * @author Anton Galaev
 */
public class AuthServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(String.valueOf(AuthServlet.class));

    private static final String AUTH_URL = "http://www.last.fm/api/auth/?api_key=599a9514090a65b21d9c7d0e47605090";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Inside doPost");
        // get login parameter, write it to session
        String login = req.getParameter("login");
        logger.info("Got login = " + login);
        req.getSession().setAttribute("login", login);


        // check if user already in the db
        UserDao userDao = new UserDao();
        User user = null;
        try {
            user = userDao.selectByLogin(login);
        } catch (SQLException e) {
            resp.sendRedirect("/error");
            return;
        }

        // if no such user, redirect to authentication page
        if (user == null) {
            logger.info("No such user in DB, redirect to authentication page");
            resp.sendRedirect(AUTH_URL);
        } else {  // or proceed to the main application page
            logger.info("User already in DB, redirect to fix page");
//            req.getSession().setAttribute("token", user.getToken());
            resp.sendRedirect("/fixfm/fix");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Inside doGet");
        // extract user parameters
        String login  = (String) req.getSession().getAttribute("login");
        String token = req.getParameter("token");
        logger.info("Authentication of user: " + login + " with token " + token);

        // create API object
        LastFmApi api = new LastFmApi();

        // create a new user and save him to db
        UserDao dao = new UserDao();
        User user = null;
        try {
            // create user
            user = new User();
            user.setLogin(login);
            // set sk field
            api.setUserSessionKey(user, token);
            // save
            dao.insert(user);
            resp.sendRedirect("/fixfm/fix");
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            resp.sendRedirect("/error");
        }
    }
}
