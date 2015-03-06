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
  public Session create(final User user_)
  {
    if (null == user_)
    {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    if (!userDAO.findUsersByUsernameOrEmail(user_.getUsername(), user_.getEmail()).isEmpty())
    {
      throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
    }

    try
    {
      final String password = Authenticator.getSecurePassword(user_.getPassword());

      user_.setPassword(password);

      user_.setRole(User.ROLE_USER);
      userDAO.insert(user_.getUsername(), user_.getPassword(), user_.getEmail(), user_.getRole(),
                     new Date(),
                     new Date());

      final User u = userDAO.findByUsername(user_.getUsername());

      final Session session = new Session(u.getUserid());
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
  public void delete(@PathParam("userid") final int userid_, @Auth final User user_)
  {
    if ((userid_ != user_.getUserid()) && !user_.getRole().equals(User.ROLE_ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    if (null != sessionDAO.findSession(user_.getUserid(), user_.getToken()))
    {
      sessionDAO.delete(user_.getUserid());
    }
    if (null != userDAO.findUser(user_.getUserid()))
    {
      userDAO.delete(user_.getUserid());
    }
  }

  @GET
  @Path("/{userid}")
  public User get(@Auth final User user_, @PathParam("userid") final int userid_)
  {
    final User u = userDAO.findUser(userid_);
    if (user_.getUserid() != userid_)
    {
      u.setEmail("private");
    }
    return u;
  }

  @GET
  public List<User> getAll(@Auth final User principal_)
  {

    if (!principal_.getRole().equals(User.ROLE_ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    return userDAO.findAll();
  }

  @PUT
  @Path("/{userid}")
  public User update(@PathParam("userid") final int userid_, @Auth final User current_, final User modified_)
  {
    try
    {
      if ((userid_ != modified_.getUserid() || userid_ != current_.getUserid()) && (null != current_.getRole() && !current_.getRole().equals(
        User.ROLE_ADMIN)))
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      final String password = Authenticator.getSecurePassword(modified_.getPassword());
      modified_.setPassword(password);
      userDAO.update(modified_.getUsername(), modified_.getPassword(), modified_.getEmail(),
                     new Date(),
                     modified_.getUserid());
      return modified_;
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
