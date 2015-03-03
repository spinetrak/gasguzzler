package net.spinetrak.gasguzzler.security;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatorTest
{
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);
  private User _user = UserTest.getUser();

  @Test
  public void authenticateReturnsUserForValidSession() throws AuthenticationException
  {
    try
    {
      when(_userDAO.findUserByUsernameAndPassword(anyString(), anyString())).thenReturn(_user);

      _userDAO.insert(_user.getUsername(), _user.getPassword(), _user.getEmail(), _user.getSalt(), _user.getRole(),
                      new Date(), new Date());
      final User user = _userDAO.findUserByUsernameAndPassword(_user.getUsername(), _user.getPassword());

      final Session session = new Session(user.getUserid());
      _sessionDAO.insert(session.getUserid(), session.getToken(), new Date());


      Authenticator authenticator = new Authenticator(_sessionDAO);

      final Optional<User> result = authenticator.authenticate(session);
      assertTrue(result.isPresent());

      Authenticator authenticator1 = new Authenticator();
      authenticator1.authenticate(session);
      fail("Expected exception");
    }
    catch (AuthenticationException ex)
    {
      assertEquals(ex.getMessage(), "Invalid credentials");
    }
  }

  @Test(expected = AuthenticationException.class)
  public void authenticateThrowsAuthenticationExceptionForInvalidCredentials() throws AuthenticationException
  {
    Authenticator authenticator = new Authenticator();

    authenticator.authenticate(new Session(1234, "failToken"));
  }

  @Test
  public void getSalt()
  {
    try
    {
      final String salt = Authenticator.getSalt();
      assertNotNull(salt);
      assertTrue(salt.length() <= 48);

      final String salt2 = Authenticator.getSalt();
      assertNotNull(salt2);
      assertNotEquals(salt, salt2);
    }
    catch (NoSuchAlgorithmException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void getSecurePassword()
  {
    try
    {
      final String password = Authenticator.getSecurePassword("password", "salt");
      assertNotEquals(password, "password");
      assertEquals(64, password.length());
      final String password2 = Authenticator.getSecurePassword("password", "salt");
      assertEquals(password, password2);
    }
    catch (NoSuchAlgorithmException e)
    {
      fail(e.getMessage());
    }


  }
}
