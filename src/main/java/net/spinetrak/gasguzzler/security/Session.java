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

package net.spinetrak.gasguzzler.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.UUID;

public class Session
{
  @NotEmpty
  @JsonProperty
  private String token;
  @NotEmpty
  @JsonProperty
  private int userid;

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
    return getToken().equals(that.getToken());

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

  @Override
  public String toString()
  {
    return "Session{" +
      "userid=" + userid +
      ", token='" + token + '\'' +
      '}';
  }
}