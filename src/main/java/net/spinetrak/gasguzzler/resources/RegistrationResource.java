package net.spinetrak.gasguzzler.resources;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.dropwizard.auth.Auth;
import net.spinetrak.gasguzzler.core.Session;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/registration")
@JsonAutoDetect
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON})
public class RegistrationResource
{
  private UserDAO userDAO;
  private SessionDAO sessionDAO;

  public RegistrationResource(UserDAO userDAO, SessionDAO sessionDAO)
  {
    super();
    this.userDAO = userDAO;
    this.sessionDAO = sessionDAO;
  }

  @POST
  public Session register(User user)
  {
    if (null == user)
    {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    if (!userDAO.findUsersByUsernameOrEmail(user.getUsername(), user.getEmail()).isEmpty())
    {
      throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
    }

    try
    {
      final String salt = Authenticator.getSalt();
      final String password = Authenticator.getSecurePassword(user.getPassword(), salt);

      user.setSalt(salt);
      user.setPassword(password);

      user.setRole(User.ROLE_USER);
      userDAO.insert(user.getUsername(), user.getPassword(), user.getEmail(), user.getSalt(), user.getRole(),
                     new Date(),
                     new Date());

      final User u = userDAO.findUserByUsernameAndPassword(user.getUsername(), user.getPassword());

      Session session = new Session(u.getUserid());
      sessionDAO.insert(session.getUserid(), session.getToken(), new java.util.Date());

      return session;
    }
    catch (Exception ex)
    {
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @DELETE
  public void unregister(@Auth User user)
  {
    if (!sessionDAO.findSession(user.getUserid(), user.getToken()).isEmpty())
    {
      sessionDAO.delete(user.getUserid());
    }
    if ( null != userDAO.findUser(user.getUserid()) )
    {
      userDAO.delete(user.getUserid());
    }
  }
}