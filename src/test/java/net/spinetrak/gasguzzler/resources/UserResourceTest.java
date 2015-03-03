package net.spinetrak.gasguzzler.resources;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserResourceTest
{
  private final Session session = new Session(0, "token");
  private final User user = UserTest.getUser();
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);
  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new UserResource(
      _userDAO,
      _sessionDAO))
    .addProvider(new SecurityProvider<>(new Authenticator(_sessionDAO)))
    .build();

  @Test
  public void create()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);
    when(_userDAO.findUsersByUsernameOrEmail(anyString(), anyString())).thenReturn(new ArrayList<User>());
    when(_userDAO.findUserByUsernameAndPassword(anyString(), anyString())).thenReturn(user);

    //TODO
    assertThat(resources.client().resource("/user")
                 .type(MediaType.APPLICATION_JSON)
                 .post(Session.class, UserTest.getUser())).isNotEqualTo(session);
  }

  @Test
  public void delete()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).delete(user);

    verify(_sessionDAO, times(2)).findSession(0, "token");
  }

  @Test
  public void get()
  {
    when(_userDAO.findUser(0)).thenReturn(user);
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    assertThat(resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).get(User.class)).isEqualTo(user);
  }

  @Test
  public void getAll()
  {
    when(_sessionDAO.findSession(0, "Admintoken")).thenReturn(session);

    resources.client().resource("/user")
      .header(SecurityProvider.TOKEN, "Admintoken").header(SecurityProvider.USERID, "0").type(
      MediaType.APPLICATION_JSON_TYPE)
      .get(new GenericType<List<User>>()
      {
      });
  }

  @Test
  public void getAllThrows401WhenNotAdminRole()
  {
    try
    {
      when(_sessionDAO.findSession(0, "token")).thenReturn(session);

      resources.client().resource("/user")
        .header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID, "0").type(
        MediaType.APPLICATION_JSON_TYPE)
        .get(new GenericType<List<User>>()
        {
        });
    }
    catch (UniformInterfaceException ex_)
    {
      assertEquals("Client response status: 401", ex_.getMessage());
    }
  }

  @Before
  public void setup()
  {
    user.setUserid(1);
    user.setRole(User.ROLE_ADMIN);
  }

  @Test
  public void update()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    assertThat(resources.client().resource("/user/0").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0")
                 .type(MediaType.APPLICATION_JSON)
                 .put(User.class, UserTest.getUser())).isEqualTo(UserTest.getUser());
  }

  @Test
  public void updateInvalidUser()
  {
    try
    {
      when(_sessionDAO.findSession(1, "token")).thenReturn(session);

      User user = UserTest.getUser();

      User updatedUser = resources.client().resource("/user/1").header(SecurityProvider.TOKEN, "token").header(
        SecurityProvider.USERID, "1")
        .type(MediaType.APPLICATION_JSON)
        .put(User.class, user);

      fail("Updated user should be invalid " + updatedUser);
    }
    catch (UniformInterfaceException ex_)
    {
      assertEquals("Client response status: 401", ex_.getMessage());
    }
  }
}
