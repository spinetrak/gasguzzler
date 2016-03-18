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

package net.spinetrak.gasguzzler.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class DataPoint
{
  @NotEmpty
  @JsonProperty
  long count;
  @NotEmpty
  @JsonProperty
  String name;
  @NotEmpty
  double rate;
  @NotEmpty
  @JsonProperty
  long timestamp;

  public long getCount()
  {
    return count;
  }

  public String getName()
  {
    return name;
  }

  public double getRate()
  {
    return rate;
  }

  public long getTimestamp()
  {
    return timestamp;
  }

  public void setCount(final long count_)
  {
    count = count_;
  }

  public void setName(final String name_)
  {
    name = name_;
  }

  public void setRate(final double rate_)
  {
    rate = rate_;
  }

  public void setTimestamp(final long timestamp_)
  {
    timestamp = timestamp_;
  }

  @Override
  public String toString()
  {
    return "DataPoint{" +
      "timestamp=" + getTimestamp() +
      ", name='" + getName() + '\'' +
      ", count=" + getCount() +
      ", rate=" + getRate() +
      '}';
  }
}
