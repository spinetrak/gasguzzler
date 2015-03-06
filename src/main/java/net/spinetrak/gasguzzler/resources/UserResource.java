package net.spinetrak.gasguzzler.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.dropwizard.auth.Auth;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/user")
@JsonAutoDetect
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON})
public class UserResource
{
  private UserDAO userDAO;
  private SessionDAO sessionDAO;

  public UserResource(UserDAO userDAO_, SessionDAO sessionDAO_)
  {
    super();
    userDAO = userDAO_;
    sessionDAO = sessionDAO_;
  }

  @POST
  public Session create(User user)
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
      final String password = Authenticator.getSecurePassword(user.getPassword());

      user.setPassword(password);

      user.setRole(User.ROLE_USER);
      userDAO.insert(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(),
                     new Date(),
                     new Date());

      final User u = userDAO.findByUsername(user.getUsername());

      Session session = new Session(u.getUserid());
      sessionDAO.insert(session.getUserid(), session.getToken(), new java.util.Date());

      return session;
    }
    catch (WebApplicationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @DELETE
  @Path("/{userid}")
  public void delete(@PathParam("userid") int userid, @Auth User user)
  {
    if ((userid != user.getUserid()) && !user.getRole().equals(User.ROLE_ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    if (null != sessionDAO.findSession(user.getUserid(), user.getToken()))
    {
      sessionDAO.delete(user.getUserid());
    }
    if (null != userDAO.findUser(user.getUserid()))
    {
      userDAO.delete(user.getUserid());
    }
  }

  @GET
  @Path("/{userid}")
  public User get(@Auth User user, @PathParam("userid") int userid)
  {
    final User u = userDAO.findUser(userid);
    if (u.getUserid() != userid)
    {
      u.setEmail("private");
    }
    return u;
  }

  @GET
  public List<User> getAll(@Auth User principal)
  {

    if (!principal.getRole().equals(User.ROLE_ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    return userDAO.findAll();
  }

  @PUT
  @Path("/{userid}")
  public User update(@PathParam("userid") int userid, @Auth User current, User modified)
  {
    try
    {
      if ((userid != modified.getUserid() || userid != current.getUserid()) && (null != current.getRole() && !current.getRole().equals(
        User.ROLE_ADMIN)))
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      final String password = Authenticator.getSecurePassword(modified.getPassword());
      modified.setPassword(password);
      userDAO.update(modified.getUsername(), modified.getPassword(), modified.getEmail(),
                     new Date(),
                     modified.getUserid());
      return modified;
    }
    catch (WebApplicationException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }
}
