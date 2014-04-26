package com.galaev.fixfm.dao;

import com.galaev.fixfm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // db credentials
    private static final String DB_URL = "jdbc:mysql://localhost/fixfm";
    private static final String DB_USER = "test";
    private static final String DB_PASS = "test";

    // sql statements
    private static final String SQL_INSERT = "insert into users (login, token) values(?, ?)";
    private static final String SQL_UPDATE = "update users set token=? where login=?";
    private static final String SQL_DELETE = "delete from users where login=?";
    private static final String SQL_SELECT = "select token from users where login=?";

    /* Database utilities */
    private ResultSet rs = null;
    private PreparedStatement ps = null;
    private Connection conn = null;


    public void insert(User user) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getToken());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    
    public void update(User user) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, user.getToken());
            ps.setString(2, user.getLogin());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    
    public void deleteByLogin(String login) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setString(1, login);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    
    public User selectByLogin(String login) throws SQLException {
        User result = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_SELECT);
            ps.setString(1, login);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                String token = resultSet.getString(1);
                result = new User();
                result.setLogin(login);
                result.setToken(token);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
        return result;
    }


    /**
     * Closes all database resources.
     */
    private void close() {
        close(rs);
        close(ps);
        close(conn);
    }

    /**
     * Closes a database resource.
     *
     * @param c db resource,
     *          such as ResultSet, Statement or Connection
     */
    private void close(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                // nothing to do here
            }
        }
    }
}
