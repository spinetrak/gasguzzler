package net.spinetrak.gasguzzler.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.UUID;

public class Session
{
  @NotEmpty
  @JsonProperty
  private int userid;
  @NotEmpty
  @JsonProperty
  private String token;

  public Session()
  {
    userid = -1;
    token = "";
  }

  public Session(int userid)
  {
    this.userid = userid;
    this.token = UUID.randomUUID().toString().substring(0, 23);
  }

  public Session(int userid, String token)
  {
    this.userid = userid;
    this.token = token;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof Session))
    {
      return false;
    }

    Session that = (Session) o;

    if (getUserid() != that.getUserid())
    {
      return false;
    }
    if (!getToken().equals(that.getToken()))
    {
      return false;
    }

    return true;
  }

  public String getToken()
  {
    return token;
  }

  public int getUserid()
  {
    return userid;
  }

  public void setToken(final String token_)
  {
    token = token_;
  }

  public void setUserid(final int userid_)
  {
    userid = userid_;
  }
}