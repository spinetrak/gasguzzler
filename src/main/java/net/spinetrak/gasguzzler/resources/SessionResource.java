package net.spinetrak.gasguzzler.resources;


import io.dropwizard.auth.Auth;
import net.spinetrak.gasguzzler.core.Session;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
  public Session login(User user_) throws Exception
  {
    String salt = userDAO.getSalt(user_.getUsername());
    if(salt == null)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    final String password = Authenticator.getSecurePassword(user_.getPassword(), salt);
    user_.setPassword(password);

    User u = userDAO.findUserByUsernameAndPassword(user_.getUsername(), user_.getPassword());
    if (null == u)
    {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    Session session = new Session(u.getUserid());
    sessionDAO.insert(session.getUserid(), session.getToken(), new java.util.Date());

    return session;
  }

  @DELETE
  public void logout(@Auth User user) throws Exception
  {
    if(!sessionDAO.findSession(user.getUserid(),user.getToken()).isEmpty())
    {
      sessionDAO.delete(user.getUserid(),user.getToken());
    }
  }
}