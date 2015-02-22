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
  private String salt;

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

  public String getToken()
  {
    return token;
  }

  public void setToken(final String token_)
  {
    token = token_;
  }

  private String token;

  public int getUserid()
  {
    return userid;
  }

  public void setUserid(final int userid_)
  {
    userid = userid_;
  }

  public String getSalt()
  {
    return salt;
  }

  public void setSalt(final String salt_)
  {
    salt = salt_;
  }
  
  public String getUsername()
  {
    return username;
  }

  public User setUsername(String username)
  {
    this.username = username;
    return this;
  }

  public String getPassword()
  {
    return password;
  }

  public User setPassword(String password)
  {
    this.password = password;
    return this;
  }

  public String getEmail()
  {
    return email;
  }

  public User setEmail(String email)
  {
    this.email = email;
    return this;
  }

  public String getRole()
  {
    return role;
  }

  public User setRole(String displayRole)
  {
    this.role = displayRole;
    return this;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof User))
    {
      return false;
    }

    User that = (User) o;

    if (!getUsername().equals(that.getUsername()))
    {
      return false;
    }
    if (!getPassword().equals(that.getPassword()))
    {
      return false;
    }
    if (!getEmail().equals(that.getEmail()))
    {
      return false;
    }
    if (!getRole().equals(that.getRole()))
    {
      return false;
    }

    return true;
  }
}
