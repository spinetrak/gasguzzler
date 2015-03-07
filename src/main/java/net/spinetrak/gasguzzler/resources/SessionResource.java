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

  public SessionResource(final UserDAO userDAO_, final SessionDAO sessionDAO_)
  {
    super();
    userDAO = userDAO_;
    sessionDAO = sessionDAO_;
  }

  @POST
  public Session create(final User user_)
  {
    try
    {
      final User u = userDAO.findByUsername(user_.getUsername());
      if (null == u)
      {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      final String storedPassword = u.getPassword();
      final String suppliedPassword = user_.getPassword();
      if (null == storedPassword || null == suppliedPassword || !Authenticator.validatePassword(suppliedPassword,
                                                                                                storedPassword))
      {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }

      final Session session = new Session(u.getUserid());
      sessionDAO.insert(session.getUserid(), session.getToken(),
                        new java.util.Date());

      return session;
    }
    catch (NoSuchAlgorithmException | InvalidKeySpecException ex_)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  @DELETE
  public void delete(@Auth final User user_)
  {
    if (null != sessionDAO.findSession(user_.getUserid(), user_.getToken()))
    {
      sessionDAO.delete(user_.getUserid(), user_.getToken());
    }
  }
}