package com.galaev.fixfm;

import com.galaev.fixfm.dao.UserDao;
import com.galaev.fixfm.model.Track;
import com.galaev.fixfm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 *
 */
public class AjaxServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(AjaxServlet.class);

    // possible responses
    private static final String FIX_DONE = "<p>Done.</p> <p>Wrong scrobbles are gone.</p> ";
    private static final String FIX_ERROR = "<p>Sorry.</p> <p>Something went wrong. Try again later.</p> ";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // extract user parameters
        String login  = (String) req.getSession().getAttribute("login");
        String token = (String) req.getSession().getAttribute("token");

        // create user
        User user = new User();
        user.setLogin(login);
        user.setToken(token);
        // save user
        UserDao dao = new UserDao();
        try {
            if (dao.selectByLogin(login) == null) {
                dao.insert(user);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        // create API object and set session key
        LastFmApi api = new LastFmApi(user);
        api.setUserSessionKey();

        // create track
        Track track = new Track();
        track.populateWithPostData(req);
        // set playcount
        api.setTrackPlaycount(track);

        api.removeTrack(track);
        api.scrobbleTrack(track);

        PrintWriter responseWriter = resp.getWriter();
        responseWriter.print(FIX_DONE);
        responseWriter.close();
    }
}
