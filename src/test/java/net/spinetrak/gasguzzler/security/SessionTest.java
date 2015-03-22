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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

public class SessionTest
{
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  public static Session getSession()
  {
    return new Session(0);
  }

  @Test
  public void deserializesFromJSON() throws Exception
  {
    assertEquals(getSession().getUserid(),
                 MAPPER.readValue(fixture("fixtures/session.json"), Session.class).getUserid());
    assertEquals(getSession().getToken().length(),
                 MAPPER.readValue(fixture("fixtures/session.json"), Session.class).getToken().length());
    assertEquals(MAPPER.readValue(fixture("fixtures/session.json"), Session.class),
                 MAPPER.readValue(fixture("fixtures/session.json"), Session.class));
  }

  @Test
  public void getTokenAndUserid()
  {
    final Session session = new Session(1234, "1234");
    assertEquals("1234", session.getToken());
    assertEquals(1234, session.getUserid());
  }

  @Test
  public void serializesToJson() throws Exception
  {
    assertEquals(fixture("fixtures/session.json").replaceAll("\\s", "").substring(
                   "{\"created\":yyyy-MM-dd HH:mm:ss,\"token\":\"244026f7-3496-4bfe-a44f\",".length() - 1),
                 MAPPER.writeValueAsString(getSession()).substring(
                   "{\"created\":yyyy-MM-dd HH:mm:ss,\"token\":\"244026f7-3496-4bfe-a44f\",".length()));
  }
}
