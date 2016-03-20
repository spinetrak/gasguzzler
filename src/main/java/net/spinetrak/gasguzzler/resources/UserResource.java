/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 spinetrak
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
import net.spinetrak.gasguzzler.core.Role;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.notifications.EmailQueue;
import net.spinetrak.gasguzzler.core.notifications.PasswordForgottenEmail;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;

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
  private final String adminEmail;
  private final UserDAO userDAO;
  private Authenticator authenticator;

  public UserResource(final UserDAO userDAO_, final Authenticator authenticator_, final String adminEmail_)
  {
    super();
    userDAO = userDAO_;
    authenticator = authenticator_;
    adminEmail = adminEmail_;
  }

  @DELETE
  @Path("/{userid}")
  public void delete(@Auth final User user_, @PathParam("userid") final int userid_)
  {
    if ((userid_ != user_.getUserid()) && (user_.getRole() != Role.ADMIN))
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    if (null != userDAO.select(user_.getUserid()))
    {
      userDAO.delete(user_);
    }
  }

  @GET
  @Path("/{userid}")
  public User get(@Auth final User user_, @PathParam("userid") final int userid_)
  {
    final User u = userDAO.select(userid_);
    u.setPassword("");
    if (user_.getUserid() != userid_)
    {
      u.setEmail("private");
    }
    return u;
  }

  @GET
  public List<User> getAll(@Auth final User principal_)
  {
    if (principal_.getRole() != Role.ADMIN)
    {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    return userDAO.select();
  }

  @POST
  public User register(final User user_)
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
      final String password = authenticator.getSecurePassword(user_.getPassword());

      user_.setPassword(password);
      user_.setRole(
        null != user_.getEmail() && adminEmail.toLowerCase().equals(user_.getEmail()) ? Role.ADMIN : Role.USER);
      userDAO.insert(user_);

      final User u = userDAO.select(user_.getUsername());
      u.setToken(authenticator.generateJWTToken(u.getUsername()));
      u.setPassword("");
      return u;

    }
    catch (final WebApplicationException ex_)
    {
      throw ex_;
    }
    catch (final Exception ex_)
    {
      throw new WebApplicationException(ex_, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @POST
  @Path("/pwreset")
  public void resetPassword(final User user_)
  {
    final String email = user_.getEmail();

    if (null == email)
    {
      throw new WebApplicationException(Response.Status.OK);
    }

    final List<User> users = userDAO.select(null, email);
    if ((users == null) || (users.size() != 1))
    {
      throw new WebApplicationException(Response.Status.OK);
    }
    try
    {
      final User user = users.get(0);


      new EmailQueue().send(new PasswordForgottenEmail(user.getEmail(), user.getUserid(),
                                                       authenticator.generateTempJWTToken(user.getUsername())));
    }
    catch (final Exception ex_)
    {
      throw new WebApplicationException(ex_, Response.Status.OK);
    }
  }

  @PUT
  @Path("/{userid}")
  public User update(@PathParam("userid") final int userid_, @Auth final User current_, final User modified_)
  {
    try
    {
      if (((userid_ != modified_.getUserid()) || (userid_ != current_.getUserid())) &&
        ((null != current_.getRole()) && (current_.getRole() != Role.ADMIN)))
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      final String password = authenticator.getSecurePassword(modified_.getPassword());
      if(password != null)
      {
        modified_.setPassword(password);
      }
      else
      {
        final User u = userDAO.select(userid_);
        modified_.setPassword(u.getPassword());
      }
      modified_.setUpdated(new Date());
      if(null == modified_.getRole())
      {
        modified_.setRole(
          null != modified_.getEmail() && adminEmail.toLowerCase().equals(
            modified_.getEmail()) ? Role.ADMIN : Role.USER);
      }
      userDAO.update(modified_);
      modified_.setPassword("");
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
