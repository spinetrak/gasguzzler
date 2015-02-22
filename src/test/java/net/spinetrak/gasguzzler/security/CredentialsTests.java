package net.spinetrak.gasguzzler.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CredentialsTests
{

  @Test
  public void getToken()
  {
    Credentials credentials = new Credentials(1234,"1234");
    assertEquals("1234", credentials.getToken());
    assertEquals(1234, credentials.getUserid());
  }
}
