package com.galaev.fixfm.dao;

import com.galaev.fixfm.model.User;

import java.sql.*;
import java.util.logging.Logger;

/**
 * This class represents a data access object for {@code User}.
 *
 * @author Anton Galaev
 */
public class UserDao {

    private static Logger logger = Logger.getLogger(String.valueOf(UserDao.class));

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

    /**
     * Insert a user into db.
     * @param user a user
     * @throws SQLException
     */
    public void insert(User user) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getToken());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    /**
     * Updates user token.
     *
     * @param user a user
     * @throws SQLException
     */
    public void update(User user) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, user.getToken());
            ps.setString(2, user.getLogin());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    /**
     * Deletes a user from db.
     *
     * @param login user login (primary key)
     * @throws SQLException
     */
    public void deleteByLogin(String login) throws SQLException {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setString(1, login);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {  // close result sets, statements and connection
            close();
        }
    }

    /**
     * Finds a user with a provided login
     *
     * @param login login user login (primary key)
     * @return user with the login
     * @throws SQLException
     */
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
            logger.severe(e.getMessage());
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
