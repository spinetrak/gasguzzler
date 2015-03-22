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
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * An example security provider that will look at each request when received by an endpoint using the auth attribute
 * and check that it has a header value containing a token and will authenticate the token to get the Principle (User)
 * for the request (otherwise throw an AuthenticationException). That Principle is the authenticated User associated
 * with the request and the resource method handling the request can use it to check authorisation to perform actions.
 *
 * @param <T> The Principle class (User) to be returned when a request is authenticated
 */
public class SecurityProvider<T> implements InjectableProvider<Auth, Parameter>
{

  public final static String TOKEN = "token";
  public final static String USERID = "userid";

  private final Authenticator<Session, T> _authenticator;

  public SecurityProvider(final Authenticator<Session, T> authenticator_)
  {
    _authenticator = authenticator_;
  }

  @Override
  public Injectable getInjectable(final ComponentContext ic, final Auth auth_, final Parameter parameter_)
  {
    return new SecurityInjectable<>(_authenticator, auth_.required());
  }

  @Override
  public ComponentScope getScope()
  {
    return ComponentScope.PerRequest;
  }

  private static class SecurityInjectable<T> extends AbstractHttpContextInjectable<T>
  {
    private final Authenticator<Session, T> _authenticator;
    private final boolean _required;

    private SecurityInjectable(final Authenticator<Session, T> authenticator_, final boolean required_)
    {
      _authenticator = authenticator_;
      _required = required_;
    }

    @Override
    public T getValue(final HttpContext context_)
    {
      // This is where the credentials are extracted from the request
      final String token = context_.getRequest().getHeaderValue(TOKEN);
      final String userid = context_.getRequest().getHeaderValue(USERID);
      try
      {
        if (token != null && userid != null)
        {
          final Optional<T> result = _authenticator.authenticate(new Session(Integer.parseInt(userid), token));
          if (result.isPresent())
          {
            return result.get();
          }
        }
      }
      catch (final NumberFormatException | AuthenticationException ex_)
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      if (_required)
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      return null;
    }
  }
}
