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
    String salt = userDAO.findSalt(user_.getUsername());
    if (salt == null)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    try
    {
      final String password = Authenticator.getSecurePassword(
        user_.getPassword(), salt);
      user_.setPassword(password);

      User u = userDAO.findUserByUsernameAndPassword(user_.getUsername(),
                                                     user_.getPassword());
      if (null == u)
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