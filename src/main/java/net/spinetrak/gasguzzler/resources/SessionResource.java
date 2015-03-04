package net.spinetrak.gasguzzler.resources;


import io.dropwizard.auth.Auth;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionResource
{

  private UserDAO userDAO;
  private SessionDAO sessionDAO;

  public SessionResource(UserDAO userDAO, SessionDAO sessionDAO)
  {
    super();
    this.userDAO = userDAO;
    this.sessionDAO = sessionDAO;
  }

  @POST
  public Session create(User user_)
  {
    try
    {
      final User u = userDAO.findByUsername(user_.getUsername());
      final String storedPassword = u.getPassword();
      final String suppliedPassword = user_.getPassword();
      if (null == storedPassword || null == suppliedPassword || !Authenticator.validatePassword(suppliedPassword,
                                                                                                storedPassword))
      {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }

      Session session = new Session(u.getUserid());
      sessionDAO.insert(session.getUserid(), session.getToken(),
                        new java.util.Date());

      return session;
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    catch (InvalidKeySpecException ex)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  @DELETE
  public void delete(@Auth User user)
  {
    if (null != sessionDAO.findSession(user.getUserid(), user.getToken()))
    {
      sessionDAO.delete(user.getUserid(), user.getToken());
    }
  }
}