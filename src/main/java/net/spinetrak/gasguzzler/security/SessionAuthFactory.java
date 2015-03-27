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

package net.spinetrak.gasguzzler.security;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.UnauthorizedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

public class SessionAuthFactory<T> extends AuthFactory<Session, T>
{
  public final static String TOKEN = "token";
  public final static String USERID = "userid";
  private final static Logger LOGGER = LoggerFactory.getLogger(SessionAuthFactory.class);
  private final Class<T> _clazz;
  private final String _realm;
  private final boolean _required;
  @Context
  private HttpServletRequest _request;
  private UnauthorizedHandler _unauthorizedHandler = new DefaultUnauthorizedHandler();


  public SessionAuthFactory(final io.dropwizard.auth.Authenticator<Session, T> authenticator_,
                            final String realm_,
                            final Class<T> clazz_)
  {
    this(false, authenticator_, realm_, clazz_);
  }

  private SessionAuthFactory(final boolean required_,
                             final io.dropwizard.auth.Authenticator<Session, T> authenticator_,
                             final String realm_,
                             final Class<T> clazz_)
  {
    super(authenticator_);
    _required = required_;
    _realm = realm_;
    _clazz = clazz_;
  }

  @Override
  public AuthFactory<Session, T> clone(final boolean required_)
  {
    return new SessionAuthFactory<>(required_, authenticator(), _realm, _clazz).responseBuilder(_unauthorizedHandler);
  }

  @Override
  public Class<T> getGeneratedClass()
  {
    return _clazz;
  }

  @Override
  public T provide()
  {
    if (null == _request)
    {
      throw new WebApplicationException(_unauthorizedHandler.buildResponse("", _realm));
    }
    try
    {
      final String token = _request.getHeader(TOKEN);
      final String userid = _request.getHeader(USERID);
      if (token != null && userid != null)
      {
        final Optional<T> result = authenticator().authenticate(new Session(Integer.parseInt(userid), token));
        if (result.isPresent())
        {
          return result.get();
        }
      }
    }
    catch (AuthenticationException e)
    {
      LOGGER.warn("Error authenticating credentials", e);
      throw new InternalServerErrorException();
    }

    if (_required)
    {
      throw new WebApplicationException(_unauthorizedHandler.buildResponse("", _realm));
    }

    return null;
  }

  public SessionAuthFactory<T> responseBuilder(final UnauthorizedHandler unauthorizedHandler_)
  {
    _unauthorizedHandler = unauthorizedHandler_;
    return this;
  }

  @Override
  public void setRequest(final HttpServletRequest request_)
  {
    _request = request_;
  }
}
