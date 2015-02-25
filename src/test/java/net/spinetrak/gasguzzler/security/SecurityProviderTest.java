package net.spinetrak.gasguzzler.security;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.LoggingFactory;
import net.spinetrak.gasguzzler.core.User;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for provider based on Dropwizard BasicAuthProviderTests
 * https://github.com/dropwizard/dropwizard/blob/master/dropwizard-auth/src/test/java/io/dropwizard/auth/basic/BasicAuthProviderTest.java
 * <p/>
 * This is where you test your provider acts as expected when injected and used by the auth attributes in a resource
 */
public class SecurityProviderTest extends JerseyTest
{

  static
  {
    LoggingFactory.bootstrap();
  }

  @Test
  public void respondsToMissingCredentialsWith401() throws Exception
  {
    try
    {
      client().resource("/test")
        .get(String.class);

      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void respondsToNonBasicCredentialsWith401() throws Exception
  {
    try
    {
      client().resource("/test")
        .header(SecurityProvider.TOKEN, "failToken")
        .get(String.class);

      fail("Should have thrown 401");
    }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Test
  public void transformsCredentialsToPrincipals() throws Exception
  {
    try
    {
      assertEquals(client().resource("/test")
                     .header(SecurityProvider.TOKEN, "validTokenReturnedAsUsername")
                     .get(String.class),
                   "validTokenReturnedAsUsername");


      fail("Expected exception");
  }
    catch (UniformInterfaceException ex)
    {
      assertEquals(ex.getResponse().getStatus(), 401);
    }
  }

  @Override
  protected AppDescriptor configure()
  {
    final DropwizardResourceConfig config = DropwizardResourceConfig.forTesting(new MetricRegistry());
    final Authenticator authenticator = new Authenticator();

    config.getSingletons().add(new SecurityProvider<>(authenticator));
    config.getSingletons().add(new ExampleResource());

    return new LowLevelAppDescriptor.Builder(config).build();
  }

  @Path("/test/")
  @Produces(MediaType.TEXT_PLAIN)
  public static class ExampleResource
  {
    @GET
    public String show(@Auth User principal)
    {
      return principal.getUsername();
    }
  }

}
