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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SessionResourceTest
{
  private final Session _session = new Session(0, "token");
  private final User _user = UserTest.getUser();
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);

  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
    .addResource(new SessionResource(
      _userDAO,
      _sessionDAO))
    .addProvider(
      AuthFactory.binder(new SessionAuthFactory<>(new Authenticator(_sessionDAO, _userDAO), "gasguzzler", User.class)))
    .build();

  @Test
  public void create()
  {
    when(_sessionDAO.select(_session)).thenReturn(_session);
    when(_userDAO.select(_user)).thenReturn(UserTest.getUserWithHashedPassword());

    final Session mysession = resources.getJerseyTest().target("/session").request()
      .post(Entity.entity(_user, MediaType.APPLICATION_JSON_TYPE), Session.class);
    assertThat(mysession).isNotEqualTo(_session);
    assertThat(mysession.getUserid()).isEqualTo(_session.getUserid());
  }

  @Test
  public void delete()
  {
    when(_userDAO.select(_session.getUserid())).thenReturn(_user);
    when(_sessionDAO.select(_session)).thenReturn(_session);

    resources.getJerseyTest().target("/session").request().header(SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0").delete();

    verify(_sessionDAO, times(2)).select(_session);
  }

  @Test
  public void testGetAll()
  {
    when(_userDAO.select(_session.getUserid())).thenReturn(UserTest.getAdminUser());
    when(_sessionDAO.select(_session)).thenReturn(_session);

    final List<Session> sessions = resources.getJerseyTest().target("/session").request().header(
      SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0").get(new GenericType<List<Session>>()
    {
    });

    assertThat(!sessions.isEmpty());
    assertThat(sessions.contains(_session));
  }
}
