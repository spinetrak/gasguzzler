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

package net.spinetrak.gasguzzler.dao;

import io.dropwizard.testing.junit.DropwizardAppRule;
import net.spinetrak.gasguzzler.Trak;
import net.spinetrak.gasguzzler.TrakConfiguration;
import net.spinetrak.gasguzzler.core.CountDataPoint;
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.core.DataPointTest;
import net.spinetrak.gasguzzler.core.RateDataPoint;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MetricsDAOTest
{
  @ClassRule
  public static final DropwizardAppRule<TrakConfiguration> RULE =
    new DropwizardAppRule<>(Trak.class, "config-test.yml");
  private MetricsDAO _metricsDAO = (MetricsDAO) RULE.getConfiguration().getDAO("metricsDAO");


  @Test
  public void createReadDelete()
  {
    final DataPoint dataPoint = DataPointTest.getDataPoint();

    _metricsDAO.insert(dataPoint);

    final List<CountDataPoint> dataPoints1 = _metricsDAO.getCount("test");

    assertTrue(!dataPoints1.isEmpty());

    final List<String> dataPoints2 = _metricsDAO.select();
    assertTrue(!dataPoints2.isEmpty());

    final int deleted = _metricsDAO.delete("test");
    assertTrue(0 < deleted);

    final List<RateDataPoint> dataPoints3 = _metricsDAO.getRate("test");
    assertTrue(dataPoints3.isEmpty());
  }

}
