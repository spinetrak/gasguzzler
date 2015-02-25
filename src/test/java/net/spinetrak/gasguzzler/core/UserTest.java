package net.spinetrak.gasguzzler.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

public class UserTest
{

  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  public static User getUser()
  {
    User user = new User();
    user.setUsername("myName");
    user.setPassword("myPassword");
    user.setRole("myDisplayRole");
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
