/*
 * The MIT License (MIT)
 *  
 * Copyright (c) 2014-2016 spinetrak
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

package net.spinetrak.gasguzzler.core.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meltmedia.jackson.crypto.Encrypted;

import javax.validation.constraints.NotNull;

public class EmailService
{
  @NotNull
  @JsonProperty
  private String endpoint;
  @NotNull
  @JsonProperty
  private String from;
  @NotNull
  @JsonProperty
  @Encrypted
  private String key;
  @NotNull
  @JsonProperty
  private String root;


  public String endpoint()
  {
    return endpoint;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof EmailService))
    {
      return false;
    }

    final EmailService that = (EmailService) o;

    if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null)
    {
      return false;
    }
    return !(from != null ? !from.equals(that.from) : that.from != null) && !(key != null ? !key.equals(
      that.key) : that.key != null) && !(root != null ? !root.equals(that.root) : that.root != null);

  }

  public String from()
  {
    return from;
  }

  @Override
  public int hashCode()
  {
    int result = endpoint != null ? endpoint.hashCode() : 0;
    result = 31 * result + (from != null ? from.hashCode() : 0);
    result = 31 * result + (key != null ? key.hashCode() : 0);
    result = 31 * result + (root != null ? root.hashCode() : 0);
    return result;
  }

  @Encrypted
  public String key()
  {
    return key;
  }

  public String root()
  {
    return root;
  }
}
