package net.spinetrak.gasguzzler.resources;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserResourceTest
{

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new UserResource())
    .addProvider(new SecurityProvider<>(new Authenticator()))
    .build();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test()
  public void delete() throws Exception
  {
    resources.client().resource("/user/test1").delete();
  }

  @Test
  public void get() throws Exception
  {
    try
    {
      resources.client().resource("/user/test1").header(SecurityProvider.TOKEN, "token").header(
        SecurityProvider.USERID, "1").get(User.class);


      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void getAll() throws Exception
  {
    try
    {
      resources.client().resource("/user")
        .header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID, "1")
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
  public void getAllThrows401WhenNotAuthenticatedToken() throws Exception
  {
    try
    {
      resources.client().resource("/user").header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID, "1")
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
        .header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID, "1")
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
  public void update() throws Exception
  {
    User user = UserTest.getUser();

    try
    {
      resources.client().resource("/user/test1").header(SecurityProvider.TOKEN, "token").header(
        SecurityProvider.USERID, "1")
        .type(MediaType.APPLICATION_JSON)
        .put(User.class, user);

      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void update_invalid_user() throws Exception
  {
    expectedException.expect(UniformInterfaceException.class);

    User user = UserTest.getUser().setUsername("");

    User updatedUser = resources.client().resource("/user/test1").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "1")
      .type(MediaType.APPLICATION_JSON)
      .put(User.class, user);

    if (updatedUser != null)
    {
      fail("Expected Exception");
    }
  }
}
