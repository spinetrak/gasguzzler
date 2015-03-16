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

  private SessionDAO sessionDAO;
  private UserDAO userDAO;

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
      final User u = userDAO.select(user_.getUsername());
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
    if (null != sessionDAO.select(user_.getUserid(), user_.getToken()))
    {
      sessionDAO.delete(user_.getUserid(), user_.getToken());
    }
  }
}