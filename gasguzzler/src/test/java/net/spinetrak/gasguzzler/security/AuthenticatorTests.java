package net.spinetrak.gasguzzler.security;

import io.dropwizard.auth.AuthenticationException;
import org.junit.Test;

public class AuthenticatorTests
{

  @Test
  public void authenticateReturnsUserForValidCredentials() throws AuthenticationException
  {
    Authenticator authenticator = new Authenticator();

    authenticator.authenticate(new Credentials(1234,"validToken"));
  }

  @Test(expected = AuthenticationException.class)
  public void authenticateThrowsAuthenticationExceptionForInvalidCredentials() throws AuthenticationException
  {
    Authenticator authenticator = new Authenticator();

    authenticator.authenticate(new Credentials(1234,"failToken"));
  }
}
