package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by spinetrak on 01/03/15.
 */
public class SessionDAOTest
{
  private SessionDAO _sessionDAO;
  private UserDAO _userDAO;

  @Test
  public void createReadDelete()
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

    final Session session = new Session(u.getUserid());
    _sessionDAO.insert(session.getUserid(), session.getToken(), new Date());
    assertEquals(_sessionDAO.findSession(session.getUserid(), session.getToken()), session);
    _sessionDAO.delete(session.getUserid());

    _userDAO.delete(u.getUserid());
  }

  @Before
  public void setup()
  {
    DBI dbi = new DBI("jdbc:postgresql://localhost/gasguzzlerdb", "gasguzzler", "gasguzzler");
    _sessionDAO = dbi.onDemand(SessionDAO.class);
    _userDAO = dbi.onDemand(UserDAO.class);
  }
}
