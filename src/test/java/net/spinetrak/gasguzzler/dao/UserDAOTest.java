/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    final List<User> users1 = _userDAO.select(user.getUsername(), user.getEmail());
    for (final User x : users1)
    {
      _userDAO.delete(x);
    }
    _userDAO.insert(user);

    final User u = _userDAO.select(user.getUsername());
    user.setUserid(u.getUserid());
    assertEquals(user, u);

    final List<User> users2 = _userDAO.select(user.getUsername(), user.getEmail());
    assertTrue(users2.size() > 0);

    final User u2 = _userDAO.select(user.getUserid());
    assertEquals(user, u2);

    user.setUsername("new_username");
    user.setUpdated(new Date());
    _userDAO.update(user);
    final User u3 = _userDAO.select(user.getUserid());
    assertEquals(user, u3);

    for (final User x : users2)
    {
      _userDAO.delete(x);
    }
  }
}
