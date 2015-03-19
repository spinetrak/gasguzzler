/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 spinetrak
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
import net.spinetrak.gasguzzler.security.Session;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    user.setCreated(new Date());
    user.setUpdated(new Date());

    final List<User> users1 = _userDAO.select(user.getUsername(), user.getEmail());
    for (final User x : users1)
    {
      _userDAO.delete(x);
    }

    _userDAO.insert(user);
    final User u = _userDAO.select(user);

    final Session session = new Session(u.getUserid());
    _sessionDAO.insert(session);
    assertEquals(_sessionDAO.select(session), session);
    _sessionDAO.delete(session);

    _userDAO.delete(u);
  }

}
