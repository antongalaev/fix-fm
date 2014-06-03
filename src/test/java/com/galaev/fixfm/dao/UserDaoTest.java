package com.galaev.fixfm.dao;

import com.galaev.fixfm.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for class UserDao.
 */
public class UserDaoTest {

    private static UserDao dao;
    private static User user;

    @BeforeClass
    public static void setUp() {
        dao = new UserDao();
        user = new User();
        user.setLogin("login");
        user.setToken("token");
    }

    @Test
    public void testDao() throws Exception {
    }


}
