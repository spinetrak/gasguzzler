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

  private final Authenticator<Session, T> authenticator;

  public SecurityProvider(Authenticator<Session, T> authenticator)
  {
    this.authenticator = authenticator;
  }

  @Override
  public Injectable getInjectable(ComponentContext ic, Auth auth, Parameter parameter)
  {
    return new SecurityInjectable<>(authenticator, auth.required());
  }

  @Override
  public ComponentScope getScope()
  {
    return ComponentScope.PerRequest;
  }

  private static class SecurityInjectable<T> extends AbstractHttpContextInjectable<T>
  {

    private final Authenticator<Session, T> authenticator;
    private final boolean required;

    private SecurityInjectable(Authenticator<Session, T> authenticator, boolean required)
    {
      this.authenticator = authenticator;
      this.required = required;
    }

    @Override
    public T getValue(HttpContext c)
    {
      // This is where the credentials are extracted from the request
      final String token = c.getRequest().getHeaderValue(TOKEN);
      final String userid = c.getRequest().getHeaderValue(USERID);
      try
      {
        if (token != null && userid != null)
        {
          final Optional<T> result = authenticator.authenticate(new Session(Integer.parseInt(userid), token));
          if (result.isPresent())
          {
            return result.get();
          }
        }
      }
      catch (NumberFormatException ex)
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
      catch (AuthenticationException ex)
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      if (required)
      {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      return null;
    }
  }
}
