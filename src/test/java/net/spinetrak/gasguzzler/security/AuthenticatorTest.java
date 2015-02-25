package net.spinetrak.gasguzzler.security;

import io.dropwizard.auth.AuthenticationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AuthenticatorTest
{

  @Test
  public void authenticateReturnsUserForValidCredentials() throws AuthenticationException
  {
    try
    {
    Authenticator authenticator = new Authenticator();

    authenticator.authenticate(new Credentials(1234,"validToken"));

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

    authenticator.authenticate(new Credentials(1234,"failToken"));
  }
}
