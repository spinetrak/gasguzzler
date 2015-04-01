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

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.Session;
import net.spinetrak.gasguzzler.security.SessionAuthFactory;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MetricsResourceTest
{
  private final Session _session = new Session(0, "token");
  private User _adminUser = UserTest.getAdminUser();
  private MetricsDAO _metricsDAO = mock(MetricsDAO.class);
  private SessionDAO _sessionDAO = mock(SessionDAO.class);
  private UserDAO _userDAO = mock(UserDAO.class);


  @Rule
  public ResourceTestRule resources = ResourceTestRule.builder()
    .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
    .addResource(new MetricsResource(
      _metricsDAO,
      _sessionDAO))
    .addProvider(
      AuthFactory.binder(new SessionAuthFactory<>(new Authenticator(_sessionDAO, _userDAO), "gasguzzler", User.class)))
    .build();


  @Test
  public void getAvailableMetrics()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);

    resources.getJerseyTest().target("/metrics").request().header(SessionAuthFactory.TOKEN, "token").header(
      SessionAuthFactory.USERID, "0").get(new GenericType<List<DataPoint>>()
    {
    });
    verify(_metricsDAO, times(1)).get();
    verify(_sessionDAO, times(2)).select(_session);
  }

  @Test
  public void getCountMetrics()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);
    resources.getJerseyTest().target("/metrics/ch.qos.logback.core.Appender.info/counts").request().header(
      SessionAuthFactory.TOKEN,
      "token").header(
      SessionAuthFactory.USERID, "0").get(new GenericType<List<DataPoint>>()
    {
    });
    verify(_metricsDAO, times(1)).getCount("ch.qos.logback.core.Appender.info");
    verify(_sessionDAO, times(2)).select(_session);
  }

  @Test
  public void getRateMetrics()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);
    resources.getJerseyTest().target("/metrics/ch.qos.logback.core.Appender.info/rates").request().header(
      SessionAuthFactory.TOKEN,
      "token").header(
      SessionAuthFactory.USERID, "0").get(new GenericType<List<DataPoint>>()
    {
    });
    verify(_metricsDAO, times(1)).getRate("ch.qos.logback.core.Appender.info");
    verify(_sessionDAO, times(2)).select(_session);
  }
}
