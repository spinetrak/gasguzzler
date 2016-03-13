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

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.AuthenticatorTest;
import net.spinetrak.gasguzzler.security.Authorizer;
import net.spinetrak.gasguzzler.security.Session;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionResourceTest
{
  private final Session _session = new Session(1, "token");
  private final User _user = UserTest.getUser();
  private final User _admin = UserTest.getAdminUser();
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);


  @Rule
  public ResourceTestRule rule = ResourceTestRule
    .builder()
    .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
    .addProvider(new AuthDynamicFeature(
      new JWTAuthFilter.Builder<User>()
        .setTokenParser(new DefaultJsonWebTokenParser())
        .setTokenVerifier(new HmacSHA512Verifier(AuthenticatorTest.SECRET_KEY))
        .setRealm("realm")
        .setPrefix("Bearer")
        .setAuthenticator(new Authenticator(_sessionDAO, _userDAO))
        .setAuthorizer(new Authorizer())
        .buildAuthFilter()))
    .addProvider(RolesAllowedDynamicFeature.class)
    .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
    .addResource(new SessionResource(
      _userDAO,
      _sessionDAO))
    .build();

  @Test
  public void create()
  {
    when(_sessionDAO.select(_session)).thenReturn(_session);
    when(_userDAO.select(_user.getUsername())).thenReturn(UserTest.getUserWithHashedPassword());

    final Session mysession = rule.getJerseyTest().target("/session").request()
      .post(Entity.entity(_user, MediaType.APPLICATION_JSON_TYPE), Session.class);
    assertThat(mysession).isNotEqualTo(_session);
    assertThat(mysession.getUserid()).isEqualTo(_session.getUserid());
  }


  @Test
  public void delete()
  {
    when(_userDAO.select(_user.getUsername())).thenReturn(_user);
    when(_sessionDAO.select(_session)).thenReturn(_session);

    rule.getJerseyTest().target("/session").request().header(AUTHORIZATION,
                                                                  "Bearer " + AuthenticatorTest.getRegularUserValidToken()).delete();

  }

  @Test
  public void testGetAll()
  {
    when(_userDAO.select(_admin.getUsername())).thenReturn(UserTest.getAdminUser());
    when(_sessionDAO.select(_session)).thenReturn(_session);

    final List<Session> sessions = rule.getJerseyTest().target("/session").request().header(AUTHORIZATION,
                                                                                                 "Bearer " + AuthenticatorTest.getAdminUserValidToken()).get(
      new GenericType<List<Session>>()
      {
      });

    assertThat(!sessions.isEmpty());
    assertThat(sessions.contains(_session));
  }

}
