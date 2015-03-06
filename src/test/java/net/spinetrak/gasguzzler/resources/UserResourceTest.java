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

package net.spinetrak.gasguzzler.resources;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserResourceTest
{
  private final Session session = new Session(0, "token");
  private final User user = UserTest.getUser();
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);
  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new UserResource(
      _userDAO,
      _sessionDAO))
    .addProvider(new SecurityProvider<>(new Authenticator(_sessionDAO)))
    .build();

  @Test
  public void create()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);
    when(_userDAO.findUsersByUsernameOrEmail(anyString(), anyString())).thenReturn(new ArrayList<User>());
    when(_userDAO.findByUsername(anyString())).thenReturn(user);

    final Session mysession = resources.client().resource("/user")
      .type(MediaType.APPLICATION_JSON)
      .post(Session.class, UserTest.getUser());
    assertThat(mysession).isNotEqualTo(session);
    assertThat(mysession.getUserid()).isEqualTo(session.getUserid());
  }

  @Test
  public void delete()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).delete(user);

    verify(_sessionDAO, times(2)).findSession(0, "token");
  }

  @Test
  public void get()
  {
    when(_userDAO.findUser(0)).thenReturn(user);
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    assertThat(resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).get(User.class)).isEqualTo(user);
  }

  @Test
  public void getAll()
  {
    when(_sessionDAO.findSession(0, "Admintoken")).thenReturn(session);

    resources.client().resource("/user")
      .header(SecurityProvider.TOKEN, "Admintoken").header(SecurityProvider.USERID, "0").type(
      MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<List<User>>()
      {
      });
  }

  @Test
  public void getAllThrows401WhenNotAdminRole()
  {
    try
    {
      when(_sessionDAO.findSession(0, "token")).thenReturn(session);

      resources.client().resource("/user")
        .header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID, "0").type(
        MediaType.APPLICATION_JSON_TYPE)
        .get(new GenericType<List<User>>()
        {
        });
    }
    catch (UniformInterfaceException ex_)
    {
      assertEquals("Client response status: 401", ex_.getMessage());
    }
  }

  @Before
  public void setup()
  {
    user.setRole(User.ROLE_ADMIN);
  }

  @Test
  public void update()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    assertThat(resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0")
                 .type(MediaType.APPLICATION_JSON)
                 .put(User.class, UserTest.getUser())).isEqualTo(UserTest.getUser());
  }

  @Test
  public void updateInvalidUser()
  {
    try
    {
      when(_sessionDAO.findSession(0, "token")).thenReturn(session);

      User user = UserTest.getUser();

      User updatedUser = resources.client().resource("/user/1").header(SecurityProvider.TOKEN, "token").header(
        SecurityProvider.USERID, "0")
        .type(MediaType.APPLICATION_JSON)
        .put(User.class, user);

      fail("Updated user should be invalid " + updatedUser);
    }
    catch (UniformInterfaceException ex_)
    {
      assertEquals("Client response status: 401", ex_.getMessage());
    }
  }
}
