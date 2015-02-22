package net.spinetrak.gasguzzler.resources;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTests;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserResourceTests
{

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new UserResource())
    .addProvider(new SecurityProvider<>(new Authenticator()))
    .build();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();


  @Test
  public void getAll() throws Exception
  {
    List<User> users = resources.client().resource("/user")
      .header(SecurityProvider.TOKEN, "validAdminToken")
      .get(new GenericType<List<User>>()
      {
      });
    assertEquals(2, users.size());
    assertEquals("user1", users.get(0).getUsername());
  }

  @Test
  public void getAllThrows401WhenNotAuthenticatedToken() throws Exception
  {
    try
    {
      resources.client().resource("/user")
        .get(new GenericType<List<User>>()
        {
        });

      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void getAllThrows401WhenPrincipalNotDisplayRoleAdmin() throws Exception
  {
    try
    {
      resources.client().resource("/user")
        .header(SecurityProvider.TOKEN, "validBasicToken")
        .get(new GenericType<List<User>>()
        {
        });

      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void get() throws Exception
  {
    User user = resources.client().resource("/user/test1").get(User.class);
    assertEquals("test1", user.getUsername());
  }

  @Test
  public void update() throws Exception
  {
    User user = UserTests.getUser();

    User updatedUser = resources.client().resource("/user/test1")
      .type(MediaType.APPLICATION_JSON)
      .put(User.class, user);

    assertEquals(user, updatedUser);
  }

  @Test
  public void update_invalid_user() throws Exception
  {
    expectedException.expect(ConstraintViolationException.class);

    User user = UserTests.getUser().setUsername("");

    User updatedUser = resources.client().resource("/user/test1")
      .type(MediaType.APPLICATION_JSON)
      .put(User.class, user);
  }

  @Test()
  public void add() throws Exception
  {
    User newUser = UserTests.getUser();

    User user = resources.client().resource("/user")
      .type(MediaType.APPLICATION_JSON)
      .post(User.class, newUser);

    assertEquals(newUser, user);
  }

  @Test()
  public void add_invalid_user() throws Exception
  {
    expectedException.expect(ConstraintViolationException.class);

    User newUser = UserTests.getUser().setUsername(null);

    User user = resources.client().resource("/user")
      .type(MediaType.APPLICATION_JSON)
      .post(User.class, newUser);
  }

  @Test()
  public void delete() throws Exception
  {
    resources.client().resource("/user/test1").delete();
  }
}
