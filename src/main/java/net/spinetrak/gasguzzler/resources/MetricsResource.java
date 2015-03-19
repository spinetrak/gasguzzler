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
import net.spinetrak.gasguzzler.core.CountDataPoint;
import net.spinetrak.gasguzzler.core.RateDataPoint;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import net.spinetrak.gasguzzler.dao.SessionDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MetricsResource
{

  private MetricsDAO metricsDAO;
  private SessionDAO sessionDAO;

  public MetricsResource(final MetricsDAO metricsDAO_, final SessionDAO sessionDAO_)
  {
    super();
    metricsDAO = metricsDAO_;
    sessionDAO = sessionDAO_;
  }

  @GET
  public List<String> get(@Auth final User user_)
  {
    if (null != sessionDAO.select(user_.getUserid(), user_.getToken()))
    {
      return metricsDAO.get();
    }
    throw new WebApplicationException(Response.Status.FORBIDDEN);
  }

  @GET
  @Path("/{name}/counts")
  public List<CountDataPoint> getCounts(@Auth final User user_, @PathParam("name") final String name_)
  {
    if (null != sessionDAO.select(user_.getUserid(), user_.getToken()))
    {
      return metricsDAO.getCount(name_);
    }
    throw new WebApplicationException(Response.Status.FORBIDDEN);
  }

  @GET
  @Path("/{name}/rates")
  public List<RateDataPoint> getRates(@Auth final User user_, @PathParam("name") final String name_)
  {
    if (null != sessionDAO.select(user_.getUserid(), user_.getToken()))
    {
      return metricsDAO.getRate(name_);
    }
    throw new WebApplicationException(Response.Status.FORBIDDEN);
  }
}