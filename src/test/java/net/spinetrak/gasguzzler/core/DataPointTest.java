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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.util.Date;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

public class DataPointTest
{

  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  public static DataPoint getDataPoint()
  {
    final DataPoint dataPoint = new DataPoint();
    dataPoint.setTimestamp(new Date().getTime());
    dataPoint.setName("test");
    dataPoint.setCount(10);
    return dataPoint;
  }


  @Test
  public void deserializesFromJSON() throws Exception
  {
    assertEquals(getDataPoint().getCount(),
                 MAPPER.readValue(fixture("fixtures/datapoint.json"), DataPoint.class).getCount());
    assertEquals(getDataPoint().getRate(),
                 MAPPER.readValue(fixture("fixtures/datapoint.json"), DataPoint.class).getRate(), 0);
    assertEquals(getDataPoint().getName(),
                 MAPPER.readValue(fixture("fixtures/datapoint.json"), DataPoint.class).getName());
  }


  @Test
  public void serializesToJson() throws Exception
  {
    assertEquals(fixture("fixtures/datapoint.json").replaceAll("\\s", "").substring(0, 40),
                 MAPPER.writeValueAsString(getDataPoint()).substring(
                   0, 40));
  }

  @Test
  public void testToString()
  {
    assertEquals(getDataPoint().toString().substring(33),
                 "DataPoint{timestamp=1426360549031, name='test', count=10, rate=0.0}".substring(33));
  }
}
