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

package net.spinetrak.gasguzzler.security;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import org.joda.time.DateTime;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenUtils.bytesOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatorTest
{
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private User _user = UserTest.getUser();
  private UserDAO _userDAO = mock(UserDAO.class);
  private static final String ORDINARY_USER = "ordinary-guy";
  public static final byte[] SECRET_KEY = bytesOf("MySecretKey");

  public static String getRegularUserValidToken() {
    final JsonWebToken token =  getJWT(UserTest.getUser().getUsername());
    return new HmacSHA512Signer(SECRET_KEY).sign(token);
  }

  public static String getAdminUserValidToken() {
    final JsonWebToken token =  getJWT(UserTest.getAdminUser().getUsername());
    return new HmacSHA512Signer(SECRET_KEY).sign(token);
  }

  private static JsonWebToken getJWT(final String user_)
{
  return JsonWebToken.builder()
    .header(JsonWebTokenHeader.HS512())
    .claim(JsonWebTokenClaim.builder().subject(user_).build())
    .build();
}
  private static JsonWebToken getInvalidJWT(final String user_)
  {
    return JsonWebToken.builder()
      .header(JsonWebTokenHeader.HS512())
      .claim(JsonWebTokenClaim.builder().subject(user_).expiration(new DateTime(0)).build())
      .build();
  }

  @Test
  public void authenticateReturnsUserForValidSession() throws AuthenticationException
  {
    try
    {
      when(_userDAO.select(_user.getUsername())).thenReturn(_user);

      _userDAO.insert(_user);
      final User user = _userDAO.select(_user.getUsername());

      final Session session = new Session(user.getUserid());
      _sessionDAO.insert(session);


      final Authenticator authenticator = new Authenticator(_sessionDAO, _userDAO);

      final Optional<User> result = authenticator.authenticate(getJWT(UserTest.getUser().getUsername()));
      assertTrue(result.isPresent());

      final Authenticator authenticator1 = new Authenticator();
      authenticator1.authenticate(null);
      fail("Expected exception");
    }
    catch (final AuthenticationException ex_)
    {
      assertEquals(ex_.getMessage(), "Invalid credentials");
    }
  }

  @Test
  public void authenticateThrowsAuthenticationExceptionForInvalidCredentials() throws AuthenticationException
  {
    try
    {
      when(_userDAO.select(_user.getUsername())).thenReturn(_user);

      final Authenticator authenticator = new Authenticator(_sessionDAO, _userDAO);

      final JsonWebToken jwt = getInvalidJWT(_user.getUsername());
      authenticator.authenticate(jwt);
      fail("Expected exception");
    }
    catch (final AuthenticationException ex_)
    {
      assertEquals(ex_.getMessage(), "com.github.toastshaman.dropwizard.auth.jwt.exceptions.TokenExpiredException: The token has expired");
    }
  }


  @Test
  public void getSecurePassword()
  {
    try
    {
      final String password = Authenticator.getSecurePassword("password");
      assertNotEquals(password, "password");
      assertTrue("Got unexpected length of " + password.length(), 220 >= password.length());
      final String password2 = Authenticator.getSecurePassword("password");
      assertNotEquals(password, password2);

      assertTrue(Authenticator.validatePassword("password", password));
      assertTrue(Authenticator.validatePassword("password", password2));

    }
    catch (final NoSuchAlgorithmException | InvalidKeySpecException e)
    {
      fail(e.getMessage());
    }
  }
}
