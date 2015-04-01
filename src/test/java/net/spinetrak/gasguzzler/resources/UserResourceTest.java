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

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.Session;
import net.spinetrak.gasguzzler.security.SessionAuthFactory;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserResourceTest
{
  private final User _adminUser = UserTest.getAdminUser();
  private final Session _session = new Session(0, "token");
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);

  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
    .addResource(new UserResource(
      _userDAO,
      _sessionDAO,
      "admin@example.com"))
    .addProvider(
      AuthFactory.binder(new SessionAuthFactory<>(new Authenticator(_sessionDAO, _userDAO), "gasguzzler", User.class)))
      //.addProvider(GeneralExceptionMapper.class)
    .build();

  @Test
  public void create()
  {
    when(_sessionDAO.select(_session)).thenReturn(_session);
    when(_userDAO.select(anyString(), anyString())).thenReturn(new ArrayList<>());
    when(_userDAO.select(_adminUser)).thenReturn(_adminUser);


    final Session mysession = resources.getJerseyTest().target("/user").request(MediaType.APPLICATION_JSON)
      .post(Entity.entity(UserTest.getUser(), MediaType.APPLICATION_JSON), Session.class);
    assertThat(mysession).isNotEqualTo(_session);
    assertThat(mysession.getUserid()).isEqualTo(_session.getUserid());
  }

  @Test
  public void delete()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);

    resources.getJerseyTest().target("/user/0").request().header(SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0").delete();

    verify(_sessionDAO, times(2)).select(_session);
  }

  @Test
  public void get()
  {
    when(_userDAO.select(0)).thenReturn(_adminUser);
    when(_sessionDAO.select(_session)).thenReturn(_session);

    assertThat(resources.getJerseyTest().target("/user/0").request().header(SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0").get(User.class)).isEqualTo(_adminUser);
  }

  @Test
  public void getAll()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);

    final List<User> allUsers = resources.getJerseyTest().target("/user").
      request().header(SessionAuthFactory.TOKEN, "token").header(SessionAuthFactory.USERID, "0")
      .get(new GenericType<List<User>>()
      {
      });

    assertThat(!allUsers.isEmpty());
    assertThat(allUsers.contains(_adminUser));
  }

  @Test
  public void getAllThrows401WhenNotAdminRole()
  {
    when(_sessionDAO.select(_session)).thenReturn(_session);

    try
    {
      final List<User> allUsers = resources.getJerseyTest().target("/user").request()
        .header(SessionAuthFactory.TOKEN, "token").header(SessionAuthFactory.USERID, "0")
        .get(new GenericType<List<User>>()
        {
        });
      fail("allUsers should be invalid" + allUsers);
    }
    catch (final Exception ex_)
    {
      assertThat(ex_).isInstanceOf(NotAuthorizedException.class);
    }
  }

  @Test
  public void update()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);

    assertThat(resources.getJerseyTest().target("/user/0").request().header(SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0")
                 .put(Entity.entity(UserTest.getUser(), MediaType.APPLICATION_JSON_TYPE), User.class)).isEqualTo(
      UserTest.getUser());
  }

  @Test
  public void updateInvalidUser()
  {
    when(_sessionDAO.select(_session)).thenReturn(_session);

    final User user = UserTest.getUser();

    try
    {
      final User invalidUser = resources.getJerseyTest().target("/user/1").request().header(SessionAuthFactory.TOKEN,
                                                                                            "token").header(
        SessionAuthFactory.USERID, "0")
        .put(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE), User.class);

      fail("invalidUser should be invalid" + invalidUser);
    }
    catch (final Exception ex_)
    {
      assertThat(ex_).isInstanceOf(NotAuthorizedException.class);
    }
  }
}
