package net.spinetrak.gasguzzler.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

/**
 * Created by spinetrak on 01/03/15.
 */
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
    Session session = new Session(1234, "1234");
    assertEquals("1234", session.getToken());
    assertEquals(1234, session.getUserid());
  }

  @Test
  public void serializesToJson() throws Exception
  {
    assertEquals(fixture("fixtures/session.json").replaceAll("\\s", "").substring(0, 20),
                 MAPPER.writeValueAsString(getSession()).substring(
                   0, 20));
  }
}
