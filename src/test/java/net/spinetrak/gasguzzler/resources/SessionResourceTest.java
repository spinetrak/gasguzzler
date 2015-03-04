package net.spinetrak.gasguzzler.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SessionResourceTest
{
  private final Session session = new Session(0, "token");
  private final User user = UserTest.getUser();
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);

  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .addResource(new SessionResource(
      _userDAO,
      _sessionDAO))
    .addProvider(new SecurityProvider<>(new Authenticator(_sessionDAO)))
    .build();

  @Test
  public void create()
  {

    when(_sessionDAO.findSession(0, "token")).thenReturn(session);
    when(_userDAO.findByUsername(anyString())).thenReturn(UserTest.getUserWithHashedPassword());
    
    final Session mysession = resources.client().resource("/session")
      .type(MediaType.APPLICATION_JSON)
      .post(Session.class, user);
    assertThat(mysession).isNotEqualTo(session);
    assertThat(mysession.getUserid()).isEqualTo(session.getUserid());
  }

  @Test
  public void delete()
  {
    when(_sessionDAO.findSession(0, "token")).thenReturn(session);

    resources.client().resource("/session").header(SecurityProvider.TOKEN, "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).delete(user);

    verify(_sessionDAO, times(2)).findSession(0, "token");
  }
}
