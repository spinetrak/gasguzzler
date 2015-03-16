/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
  private SessionDAO sessionDAO;
  private UserDAO userDAO;

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

    if (!userDAO.select(user_.getUsername(), user_.getEmail()).isEmpty())
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

      final User u = userDAO.select(user_.getUsername());

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
    if (null != sessionDAO.select(user_.getUserid(), user_.getToken()))
    {
      sessionDAO.delete(user_.getUserid());
    }
    if (null != userDAO.select(user_.getUserid()))
    {
      userDAO.delete(user_.getUserid());
    }
  }

  @GET
  @Path("/{userid}")
  public User get(@Auth final User user_, @PathParam("userid") final int userid_)
  {
    final User u = userDAO.select(userid_);
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

    return userDAO.select();
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
