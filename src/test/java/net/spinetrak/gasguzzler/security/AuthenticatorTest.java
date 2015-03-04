package net.spinetrak.gasguzzler.security;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
      when(_userDAO.findByUsername(anyString())).thenReturn(_user);

      _userDAO.insert(_user.getUsername(), _user.getPassword(), _user.getEmail(), _user.getRole(),
                      new Date(), new Date());
      final User user = _userDAO.findByUsername(_user.getUsername());

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
  public void getSecurePassword()
  {
    try
    {
      final String password = Authenticator.getSecurePassword("password");
      assertNotEquals(password, "password");
      assertTrue("Got unexpected length of " + password.length(), 200 >= password.length());
      final String password2 = Authenticator.getSecurePassword("password");
      assertNotEquals(password, password2);

      assertTrue(Authenticator.validatePassword("password", password));
      assertTrue(Authenticator.validatePassword("password", password2));

    }
    catch (NoSuchAlgorithmException e)
    {
      fail(e.getMessage());
    }
    catch (InvalidKeySpecException e)
    {
      fail(e.getMessage());
    }

  }
}
