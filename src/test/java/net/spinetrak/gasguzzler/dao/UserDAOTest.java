package net.spinetrak.gasguzzler.dao;

import io.dropwizard.testing.junit.DropwizardAppRule;
import net.spinetrak.gasguzzler.Trak;
import net.spinetrak.gasguzzler.TrakConfiguration;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spinetrak on 01/03/15.
 */
public class UserDAOTest
{
  @ClassRule
  public static final DropwizardAppRule<TrakConfiguration> RULE =
    new DropwizardAppRule<>(Trak.class, "config-test.yml");
  private UserDAO _userDAO = (UserDAO) RULE.getConfiguration().getDAO("userDAO");
  
  @Test
  public void createReadUpdateDelete()
  {
    final User user = UserTest.getUser();

    _userDAO.insert(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), new Date(),
                    new Date());

    final User u = _userDAO.findByUsername(user.getUsername());
    user.setUserid(u.getUserid());
    assertEquals(user, u);

    final List<User> users = _userDAO.findUsersByUsernameOrEmail(user.getUsername(), user.getEmail());
    assertTrue(users.size() > 0);

    final User u2 = _userDAO.findUser(user.getUserid());
    assertEquals(user, u2);

    user.setUsername("new_username");
    _userDAO.update(user.getUsername(), user.getPassword(), user.getEmail(), new Date(),
                    user.getUserid());
    final User u3 = _userDAO.findUser(user.getUserid());
    assertEquals(user, u3);

    for (User x : users)
    {
      _userDAO.delete(x.getUserid());
    }
  }
}
