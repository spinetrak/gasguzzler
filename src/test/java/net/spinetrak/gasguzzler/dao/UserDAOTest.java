package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.User;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spinetrak on 01/03/15.
 */
public class UserDAOTest
{
  private UserDAO _userDAO;

  @Test
  public void createReadUpdateDelete()
  {
    final User user = new User();
    user.setUsername("username");
    user.setPassword("pwd");
    user.setToken("token");
    user.setEmail("a@b.c");
    user.setSalt("salt");
    user.setRole(User.ROLE_USER);

    _userDAO.insert(user.getUsername(), user.getPassword(), user.getEmail(), user.getSalt(), user.getRole(), new Date(),
                    new Date());

    final User u = _userDAO.findUserByUsernameAndPassword(user.getUsername(), user.getPassword());
    user.setUserid(u.getUserid());
    assertEquals(user, u);

    final List<User> users = _userDAO.findUsersByUsernameOrEmail(user.getUsername(), user.getEmail());
    assertTrue(users.size() > 0);

    final User u2 = _userDAO.findUser(user.getUserid());
    assertEquals(user, u2);

    final String salt = _userDAO.findSalt(user.getUsername());
    assertEquals(user.getSalt(), salt);

    user.setUsername("new_username");
    _userDAO.update(user.getUsername(), user.getPassword(), user.getEmail(), user.getSalt(), new Date(),
                    user.getUserid());
    final User u3 = _userDAO.findUser(user.getUserid());
    assertEquals(user, u3);

    for (User x : users)
    {
      _userDAO.delete(x.getUserid());
    }
  }

  @Before
  public void setup()
  {
    DBI dbi = new DBI("jdbc:postgresql://localhost/gasguzzlerdb", "gasguzzler", "gasguzzler");
    _userDAO = dbi.onDemand(UserDAO.class);
  }
}
