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

import java.util.Date;
import java.util.UUID;

public class Session
{
  private Date date;
  @NotEmpty
  @JsonProperty
  private String token;
  @NotEmpty
  @JsonProperty
  private int userid;

  public Session()
  {
    this(-1, "");
  }

  public Session(final int userid_)
  {
    this(userid_, UUID.randomUUID().toString().substring(0, 23));
  }

  public Session(final int userid_, final String token_)
  {
    this(userid_, token_, new Date());
  }

  public Session(final int userid_, final String token_, final Date date_)
  {
    userid = userid_;
    token = token_;
    date = date_;
  }

  @Override
  public boolean equals(final Object obj_)
  {
    if (this == obj_)
    {
      return true;
    }
    if (!(obj_ instanceof Session))
    {
      return false;
    }

    final Session that = (Session) obj_;

    return getUserid() == that.getUserid() && getToken().equals(that.getToken());

  }

  public Date getDate()
  {
    return date;
  }

  public String getToken()
  {
    return token;
  }

  public int getUserid()
  {
    return userid;
  }

  public void setDate(final Date date_)
  {
    date = date_;
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