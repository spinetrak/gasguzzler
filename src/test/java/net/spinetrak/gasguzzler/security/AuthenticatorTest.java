package net.spinetrak.gasguzzler.security;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.junit.Assert.*;

public class AuthenticatorTest
{
  private UserDAO _userDAO;
  private SessionDAO _sessionDAO;
  private User _user;

  @Test
  public void authenticateReturnsUserForValidSession() throws AuthenticationException
  {
    User user = null;
    try
    {
      _userDAO.insert(_user.getUsername(), _user.getPassword(), _user.getEmail(), _user.getSalt(), _user.getRole(),
                      new Date(), new Date());
      user = _userDAO.findUserByUsernameAndPassword(_user.getUsername(), _user.getPassword());

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
    finally
    {
      if (user != null)
      {
        _sessionDAO.delete(user.getUserid());
        _userDAO.delete(user.getUserid());
      }
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

  @Before
  public void setup()
  {
    DBI dbi = new DBI("jdbc:postgresql://localhost/gasguzzlerdb", "gasguzzler", "gasguzzler");
    _userDAO = dbi.onDemand(UserDAO.class);
    _sessionDAO = dbi.onDemand(SessionDAO.class);
    _user = new User();
    _user.setUsername("username");
    _user.setPassword("pwd");
    _user.setToken("token");
    _user.setEmail("a@b.c");
    _user.setSalt("salt");
    _user.setRole(User.ROLE_USER);
  }
}
