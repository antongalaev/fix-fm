package com.galaev.fixfm;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class FixFmServlet extends HttpServlet {




    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FixFmApp fixFmApp = new FixFmApp();
        fixFmApp.process(req);
        PrintWriter responseWriter = resp.getWriter();
        responseWriter.print("<p>Done.</p> <p>Wrong scrobbles are gone.</p> ");
        responseWriter.close();
    }


}
