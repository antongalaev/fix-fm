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

    private String token;
    private Object lock = new Object();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        token = req.getParameter("token");
        lock.notify();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // create app and extract all parameters
        FixFmApp fixFmApp = new FixFmApp();
        fixFmApp.extractParams(req);
        // wait for token acquiring
//        while (token == null) {
//            try {
//                lock.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        fixFmApp.setToken(token);
        // process the request
        String result = fixFmApp.process();
        // output the result
        PrintWriter responseWriter = resp.getWriter();
        responseWriter.print(result);
        responseWriter.close();
    }
}
