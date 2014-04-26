package com.galaev.fixfm.dao;

import com.galaev.fixfm.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        dao.insert(user);
        User storedUser = dao.selectByLogin(user.getLogin());
        assertEquals(user.getLogin(), storedUser.getLogin());
        assertEquals(user.getToken(), storedUser.getToken());

        user.setToken("token2");
        dao.update(user);
        storedUser = dao.selectByLogin(user.getLogin());
        assertEquals(user.getLogin(), storedUser.getLogin());
        assertEquals(user.getToken(), storedUser.getToken());

        dao.deleteByLogin(user.getLogin());
        assertNull(dao.selectByLogin(user.getLogin()));
    }

}
