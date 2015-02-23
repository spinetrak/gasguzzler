package net.spinetrak.gasguzzler.resources;

import io.dropwizard.auth.Auth;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Path("/user")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UserResource
{
  private UserDAO _userDAO;

  protected UserResource()
  {
  }

  public UserResource(UserDAO userDAO_)
  {
    super();
    _userDAO = userDAO_;
  }

  @POST
  public User add(@Valid User user)
  {
    return user;
  }

  @DELETE
  @Path("/{userid}")
  public void delete(@PathParam("userid") String userid)
  {
  }

  @GET
  @Path("/{userid}")
  public User get(@Auth User user, @PathParam("userid") int userid)
  {
    final User u = _userDAO.findUser(userid);
    if (u.getUserid() != userid)
    {
      u.setEmail("private");
    }
    return u;
  }

  /*
  * Using the Auth attribute will use the injected provider to authenticate all requests to this path
  * You can also use the principal to apply authorisation in code dynamically
   */
  @GET
  public List<User> getAll(@Auth User principal)
  {

    if (!principal.getRole().equals(User.ROLE_ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    List<User> users = new LinkedList<>();
    users.add(
      new User()
        .setUsername("user1")
        .setRole("Admin")
    );
    users.add(
      new User()
        .setUsername("user2")
        .setRole("DBA")
    );

    return users;
  }

  @PUT
  @Path("/{userid}")
  public User update(@PathParam("userid") String userid, @Auth User current, User modified)
  {
    try
    {
      final String salt = Authenticator.getSalt();
      final String password = Authenticator.getSecurePassword(modified.getPassword(), salt);

      modified.setSalt(salt);
      modified.setPassword(password);
      _userDAO.update(modified.getUsername(), modified.getPassword(), modified.getEmail(), modified.getSalt(),
                      new Date(),
                      modified.getUserid());
      return modified;
    }
    catch (Exception ex)
    {
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }
}
