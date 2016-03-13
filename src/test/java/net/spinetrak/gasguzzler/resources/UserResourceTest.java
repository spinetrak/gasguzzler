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
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.AuthenticatorTest;
import net.spinetrak.gasguzzler.security.Authorizer;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResourceTest
{
  private final User _adminUser = UserTest.getAdminUser();
  private UserDAO _userDAO = mock(UserDAO.class);
  private Authenticator _authenticator = new Authenticator(_userDAO,"secret".getBytes());

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
        .setAuthenticator(_authenticator)
        .setAuthorizer(new Authorizer())
        .buildAuthFilter()))
    .addProvider(RolesAllowedDynamicFeature.class)
    .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
    .addResource(new UserResource(
      _userDAO,
      _authenticator,
      "admin@example.com"))
    .build();

  @Test
  public void register()
  {
    when(_userDAO.select(anyString(), anyString())).thenReturn(new ArrayList<>());
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);


    final User user = rule.getJerseyTest().target("/user").request(MediaType.APPLICATION_JSON)
      .post(Entity.entity(UserTest.getAdminUser(), MediaType.APPLICATION_JSON), User.class);
    assertThat(user).isEqualTo(_adminUser);
    assertThat(3 == user.getToken().split(".").length);
  }


  @Test
  public void delete()
  {
    when(_userDAO.select(_adminUser.getUserid())).thenReturn(_adminUser);

    rule.getJerseyTest().target("/user/0").request().header(AUTHORIZATION,
                                                                 "Bearer " + AuthenticatorTest.getAdminUserValidToken()).delete();

    verify(_userDAO, times(1)).select(_adminUser.getUsername());
  }

  @Test
  public void get()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);
    when(_userDAO.select(_adminUser.getUserid())).thenReturn(_adminUser);

    assertThat(rule.getJerseyTest().target("/user/0").request().header(AUTHORIZATION,
                                                                            "Bearer " + AuthenticatorTest.getAdminUserValidToken()).get(User.class)).isEqualTo(_adminUser);
  }

  @Test
  public void getAll()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);

    final List<User> allUsers = rule.getJerseyTest().target("/user").
      request().header(AUTHORIZATION,
                       "Bearer " + AuthenticatorTest.getAdminUserValidToken())
      .get(new GenericType<List<User>>()
      {
      });

    assertThat(!allUsers.isEmpty());
    assertThat(allUsers.contains(_adminUser));
  }

  @Test
  public void getAllThrows401WhenNotAdminRole()
  {
    try
    {
      final List<User> allUsers = rule.getJerseyTest().target("/user").request()
        .header(AUTHORIZATION,
                "Bearer " + AuthenticatorTest.getRegularUserValidToken())
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
  public void testResetPassword()
  {
    final Response response = rule.getJerseyTest().target("/user/pwreset").request().post(
      Entity.entity(UserTest.getUser(), MediaType.APPLICATION_JSON));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
  
  @Test
  public void update()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);

    final User updated = rule.getJerseyTest().target("/user/0").request().header(AUTHORIZATION,
                                                                            "Bearer " + AuthenticatorTest.getAdminUserValidToken())
                 .put(Entity.entity(UserTest.getAdminUser(), MediaType.APPLICATION_JSON_TYPE), User.class);

    assertEquals(UserTest.getAdminUser(),updated);
  }

  @Test
  public void updateInvalidUser()
  {
    final User user = UserTest.getUser();

    try
    {
      final User invalidUser = rule.getJerseyTest().target("/user/1").request().header(AUTHORIZATION,
                                                                                            "Bearer " + AuthenticatorTest.getRegularUserValidToken())
        .put(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE), User.class);

      fail("invalidUser should be invalid" + invalidUser);
    }
    catch (final Exception ex_)
    {
      assertThat(ex_).isInstanceOf(NotAuthorizedException.class);
    }
  }

}
