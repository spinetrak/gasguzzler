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

package net.spinetrak.gasguzzler.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.spinetrak.gasguzzler.Trak;
import net.spinetrak.gasguzzler.TrakConfiguration;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DbReporterTest
{
  @ClassRule
  public static final DropwizardAppRule<TrakConfiguration> RULE =
    new DropwizardAppRule<>(Trak.class, "config-test.yml");
  private MetricsDAO _metricsDAO = (MetricsDAO) RULE.getConfiguration().getDAO("metricsDAO");
  private DbReporter _reporter = RULE.getConfiguration().getReporter();


  @Test
  public void reportReadDelete()
  {
    final TreeMap<String, Gauge> gauges = new TreeMap<>();
    gauges.put("test", new FileDescriptorRatioGauge());

    final TreeMap<String, Counter> counters = new TreeMap<>();
    final Counter c = new Counter();
    c.inc();
    counters.put("test", c);

    final TreeMap<String, Histogram> histograms = new TreeMap<>();
    histograms.put("test", new Histogram(new UniformReservoir()));

    final TreeMap<String, Meter> meters = new TreeMap<>();
    final Meter meter = new Meter();
    meter.mark();
    meters.put("test", meter);

    final TreeMap<String, Timer> timers = new TreeMap<>();
    final Timer timer = new Timer();
    timer.update(1, TimeUnit.SECONDS);
    timers.put("test", timer);

    _reporter.report(gauges, counters, histograms, meters, timers);

    assertTrue(0 < _metricsDAO.getCount("test").size());

    final int deleted = _metricsDAO.delete("test");
    assertTrue(0 < deleted);

    assertEquals(0, _metricsDAO.getCount("test").size());
  }

}
