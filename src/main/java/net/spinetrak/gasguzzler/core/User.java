/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.spinetrak.gasguzzler.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.spinetrak.gasguzzler.security.Session;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

public class User
{

  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";

  private Date created;
  @NotEmpty
  @JsonProperty
  private String email;
  @NotEmpty
  @JsonProperty
  private String password;
  @NotEmpty
  @JsonProperty
  private String role;
  private Session session;
  private Date updated;
  @NotEmpty
  @JsonProperty
  private int userid;
  @NotEmpty
  @JsonProperty
  private String username;

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

  public Date getCreated()
  {
    return created;
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

  public Session getSession()
  {
    return session;
  }

  public Date getUpdated()
  {
    return updated;
  }

  public int getUserid()
  {
    return userid;
  }

  public String getUsername()
  {
    return username;
  }

  public void setCreated(final Date created_)
  {
    created = created_;
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

  public void setSession(final Session session_)
  {
    session = session_;
  }

  public void setUpdated(final Date updated_)
  {
    updated = updated_;
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
