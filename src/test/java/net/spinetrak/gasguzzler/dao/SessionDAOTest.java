package net.spinetrak.gasguzzler.dao;

import io.dropwizard.testing.junit.DropwizardAppRule;
import net.spinetrak.gasguzzler.Trak;
import net.spinetrak.gasguzzler.TrakConfiguration;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by spinetrak on 01/03/15.
 */
public class SessionDAOTest
{
  @ClassRule
  public static final DropwizardAppRule<TrakConfiguration> RULE =
    new DropwizardAppRule<>(Trak.class, "config-test.yml");
  private SessionDAO _sessionDAO = (SessionDAO) RULE.getConfiguration().getDAO("sessionDAO");
  private UserDAO _userDAO = (UserDAO) RULE.getConfiguration().getDAO("userDAO");

  @Test
  public void createReadDelete()
  {
    final User user = UserTest.getUser();

    user.setRole(User.ROLE_USER);
    _userDAO.insert(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), new Date(),
                    new Date());
    final User u = _userDAO.findByUsername(user.getUsername());

    final Session session = new Session(u.getUserid());
    _sessionDAO.insert(session.getUserid(), session.getToken(), new Date());
    assertEquals(_sessionDAO.findSession(session.getUserid(), session.getToken()), session);
    _sessionDAO.delete(session.getUserid());

    _userDAO.delete(u.getUserid());
  }

}
