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

import com.sun.jersey.api.client.GenericType;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import net.spinetrak.gasguzzler.security.Session;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.List;

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
    .addResource(new MetricsResource(
      _metricsDAO,
      _sessionDAO))
    .addProvider(new SecurityProvider<>(new Authenticator(_sessionDAO, _userDAO)))
    .build();


  @Test
  public void getAvailableMetrics()
  {
    when(_sessionDAO.select(any())).thenReturn(_session);
    when(_userDAO.select(_session.getUserid())).thenReturn(_adminUser);
    resources.client().resource("/metrics").header(SecurityProvider.TOKEN, "token").header(SecurityProvider.USERID,
                                                                                           "0").type(
      MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<DataPoint>>()
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
    resources.client().resource("/metrics/ch.qos.logback.core.Appender.info/counts").header(SecurityProvider.TOKEN,
                                                                                            "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<DataPoint>>()
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
    resources.client().resource("/metrics/ch.qos.logback.core.Appender.info/rates").header(SecurityProvider.TOKEN,
                                                                                     "token").header(
      SecurityProvider.USERID, "0").type(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<DataPoint>>()
    {
    });
    verify(_metricsDAO, times(1)).getRate("ch.qos.logback.core.Appender.info");
    verify(_sessionDAO, times(2)).select(_session);
  }
}
