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

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.AuthenticatorTest;
import net.spinetrak.gasguzzler.security.Authorizer;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.mockito.Mockito.*;

public class MetricsResourceTest
{
  private User _adminUser = UserTest.getAdminUser();
  private MetricsDAO _metricsDAO = mock(MetricsDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);

  @Rule
  public ResourceTestRule rule = ResourceTestRule
    .builder()
    .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
    .addProvider(new AuthDynamicFeature(
      new JWTAuthFilter.Builder<User>()
        .setTokenParser(new DefaultJsonWebTokenParser())
        .setTokenVerifier(new HmacSHA512Verifier(AuthenticatorTest.SECRET_KEY))
        .setRealm("realm")
        .setPrefix("Bearer")
        .setAuthenticator(new Authenticator(_userDAO,"secret".getBytes()))
        .setAuthorizer(new Authorizer())
        .buildAuthFilter()))
    .addProvider(RolesAllowedDynamicFeature.class)
    .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
    .addResource(new MetricsResource(
      _metricsDAO))
    .build();


  @Test
  public void getAvailableMetrics()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);

    rule.getJerseyTest().target("/metrics").request().header(AUTHORIZATION,
                                                             "Bearer " + AuthenticatorTest.getAdminUserValidToken()).get(
      new GenericType<List<DataPoint>>()
      {
      });
    verify(_metricsDAO, times(1)).select();
  }

  @Test
  public void getCountMetrics()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);
    rule.getJerseyTest().target("/metrics/ch.qos.logback.core.Appender.info/counts").request().header(AUTHORIZATION,
                                                                                                      "Bearer " + AuthenticatorTest.getAdminUserValidToken()).get(
      new GenericType<List<DataPoint>>()
      {
      });
    verify(_metricsDAO, times(1)).getCount("ch.qos.logback.core.Appender.info");
  }

  @Test
  public void getRateMetrics()
  {
    when(_userDAO.select(_adminUser.getUsername())).thenReturn(_adminUser);
    rule.getJerseyTest().target("/metrics/ch.qos.logback.core.Appender.info/rates").request().header(AUTHORIZATION,
                                                                                                     "Bearer " + AuthenticatorTest.getAdminUserValidToken()).get(
      new GenericType<List<DataPoint>>()
      {
      });
    verify(_metricsDAO, times(1)).getRate("ch.qos.logback.core.Appender.info");
  }
}
