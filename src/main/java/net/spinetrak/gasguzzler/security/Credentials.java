package net.spinetrak.gasguzzler.security;

/**
 * This class is an example of a POJO used to hold custom credentials to be used to link a request to a specific principle (User)
 */
public class Credentials
{
  private final String token;

  private final int userid;

  public Credentials(int userid, String token)
  {
    this.userid = userid ; this.token = token;
  }

  public String getToken()
  {
    return token;
  }

  public int getUserid()
  {
    return userid;
  }
}
