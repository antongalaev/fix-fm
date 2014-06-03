package com.galaev.fixfm;

import com.galaev.fixfm.dao.UserDao;
import com.galaev.fixfm.model.Track;
import com.galaev.fixfm.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class represents a servlet,
 * intended to answer ajax requests with all params
 * required for fixing the tag.
 *
 * @author Anton Galaev
 */
public class AjaxServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(String.valueOf(AjaxServlet.class));

    // possible responses
    private static final String FIX_DONE = "<p>Done.</p> <p>Wrong scrobbles are gone.</p> ";
    private static final String FIX_ERROR = "<p>Sorry.</p> <p>Something went wrong. Try again later.</p> ";

    /**
     * Handles post requests.
     *
     * @param req request object with all fixing params
     * @param resp response object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Inside doPost");
        // extract user parameters
        String login  = (String) req.getSession().getAttribute("login");
        String token = (String) req.getSession().getAttribute("token");
        logger.info("Logged as " + login + " with token " + token);
        // create user
        User user = new User();
        user.setLogin(login);
        user.setToken(token);
        // save user if not already
        UserDao dao = new UserDao();
        try {
            if (dao.selectByLogin(login) == null) {
                dao.insert(user);
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        // create API object and set session key
        LastFmApi api = new LastFmApi(user);
        api.setUserSessionKey();

        // create track
        Track track = new Track();
        track.populateWithPostData(req);
        // set playcount
        api.setTrackPlaycount(track);
        // actually fix the problem
        api.removeTrack(track);
        api.scrobbleTrack(track);

        // write result
        PrintWriter responseWriter = resp.getWriter();
        responseWriter.print(FIX_DONE);
        responseWriter.close();
    }
}
