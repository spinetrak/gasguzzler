package net.spinetrak.gasguzzler.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class User
{

  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";


  @NotEmpty
  @JsonProperty
  private int userid;

  @NotEmpty
  @JsonProperty
  private String username;

  @NotEmpty
  @JsonProperty
  private String password;

  @NotEmpty
  @JsonProperty
  private String email;

  @NotEmpty
  @JsonProperty
  private String role;
  private String token;

  @Override
  public boolean equals(final Object object_)
  {
    if (this == object_)
    {
      return true;
    }
    if (!(object_ instanceof User))
    {
      return false;
    }

    User that = (User) object_;

    if (getUserid() != that.getUserid())
    {
      return false;
    }
    if (getUsername().equals(that.getUsername()))
    {
      if (getEmail().equals(that.getEmail()))
      {
        return true;
      }
    }
    return false;

  }

  public String getEmail()
  {
    return email;
  }

  public String getPassword()
  {
    return password;
  }

  public String getRole()
  {
    return role;
  }

  public String getToken()
  {
    return token;
  }

  public int getUserid()
  {
    return userid;
  }

  public String getUsername()
  {
    return username;
  }

  public User setEmail(final String email_)
  {
    email = email_;
    return this;
  }

  public User setPassword(final String password_)
  {
    password = password_;
    return this;
  }

  public User setRole(final String role_)
  {
    role = role_;
    return this;
  }

  public void setToken(final String token_)
  {
    token = token_;
  }

  public void setUserid(final int userid_)
  {
    userid = userid_;
  }

  public User setUsername(final String username_)
  {
    username = username_;
    return this;
  }
}
