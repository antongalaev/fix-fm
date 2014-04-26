package com.galaev.fixfm;

import com.galaev.fixfm.dao.UserDao;
import com.galaev.fixfm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(AuthServlet.class);

    private static final String AUTH_URL = "http://www.last.fm/api/auth/?api_key=599a9514090a65b21d9c7d0e47605090";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        req.getSession().setAttribute("login", login);

        // check if user already in the db
        UserDao userDao = new UserDao();
        User user = null;
        try {
            user = userDao.selectByLogin(login);
        } catch (SQLException e) {
            resp.sendRedirect("/error");
        }

        // if no such user, redirect to authentication page
        if (user == null) {
            resp.sendRedirect(AUTH_URL);
        } else {  // or proceed to the main application page
            req.getSession().setAttribute("token", user.getToken());
            resp.sendRedirect("/fix");
        }
    }
}
