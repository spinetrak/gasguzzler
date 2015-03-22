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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.spinetrak.gasguzzler.security.Session;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class User
{
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date created;
  @NotEmpty
  @JsonProperty
  private String email;
  @NotEmpty
  @JsonProperty
  private String password;
  @NotNull
  @JsonProperty
  private Role role;
  @JsonIgnore
  private Session session;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updated;
  @NotNull
  @JsonProperty
  private int userid;
  @NotEmpty
  @JsonProperty
  private String username;

  public User()
  {
    setRole(Role.USER);
  }
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

  public Role getRole()
  {
    return role;
  }

  @JsonIgnore
  public String getRoleAsString()
  {
    return role.name();
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

  public void setEmail(final String email_)
  {
    email = email_;
  }

  public void setPassword(final String password_)
  {
    password = password_;
  }

  public void setRole(final Role role_)
  {
    role = role_;
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

  public void setUsername(final String username_)
  {
    username = username_;
  }
}
