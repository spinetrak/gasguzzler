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

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.logging.LoggingFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    catch (final UniformInterfaceException ex_)
    {
      assertEquals(ex_.getResponse().getStatus(), 401);
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
    catch (final UniformInterfaceException ex_)
    {
      assertEquals(ex_.getResponse().getStatus(), 401);
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
    catch (final UniformInterfaceException ex_)
    {
      assertEquals(ex_.getResponse().getStatus(), 401);
    }
  }

  @Override
  protected AppDescriptor configure()
  {
    final DropwizardResourceConfig config = DropwizardResourceConfig.forTesting(new MetricRegistry());
    final Authenticator authenticator = new Authenticator();

    config.getSingletons().add(new SecurityProvider<>(authenticator));

    return new LowLevelAppDescriptor.Builder(config).build();
  }

}
