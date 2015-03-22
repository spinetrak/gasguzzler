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

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

public class UserTest
{

  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  public static User getAdminUser()
  {
    User admin = new User();
    admin.setRole(Role.ADMIN);
    admin.setUsername("myName");
    admin.setPassword(
      "063450be3449991e2ae85c11f8ac2721ad252cb4046b6099f2c5bfd688a63eee");
    admin.setEmail("my@mail.de");
    admin.setUserid(0);
    return admin;
  }

  public static User getUser()
  {
    User user = new User();
    user.setUsername("myName");
    user.setPassword(
      "063450be3449991e2ae85c11f8ac2721ad252cb4046b6099f2c5bfd688a63eee");
    user.setEmail("my@mail.de");
    user.setUserid(0);
    return user;
  }

  public static User getUserWithHashedPassword()
  {
    User user = new User();
    user.setUsername("myName");
    user.setPassword(
      "1000:5b42403464356230396335:f02fee958cd89aa99b9f04655db032b207da0fa352055238462f2e139edf4eee71e8dab92e541e293e8634348a0564184014e1aaf39e90e28e1a4fb8c83143b9");
    user.setEmail("my@mail.de");
    user.setUserid(0);
    return user;
  }


  @Test
  public void deserializesFromJSON() throws Exception
  {
    assertEquals(getUser(), MAPPER.readValue(fixture("fixtures/user.json"), User.class));
  }


  @Test
  public void serializesToJson() throws Exception
  {
    assertEquals(fixture("fixtures/user.json").replaceAll("\\s", ""), MAPPER.writeValueAsString(getUser()));
  }
}
