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
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class DbReporter extends ScheduledReporter
{

  private static final Logger LOGGER = LoggerFactory.getLogger(DbReporter.class);
  private Clock _clock;
  private MetricsDAO _dao;


  private DbReporter(MetricRegistry registry_,
                     MetricsDAO dao_,
                     TimeUnit rateUnit_,
                     TimeUnit durationUnit_,
                     Clock clock_,
                     MetricFilter filter_)
  {
    super(registry_, "db-reporter", filter_, rateUnit_, durationUnit_);
    _dao = dao_;
    _clock = clock_;
  }

  public static Builder forRegistry(MetricRegistry registry)
  {
    return new Builder(registry);
  }

  @Override
  public void report(final SortedMap<String, Gauge> gauges_, final SortedMap<String, Counter> counters_,
                     final SortedMap<String, Histogram> histograms_, final SortedMap<String, Meter> meters_,
                     final SortedMap<String, Timer> timers_)
  {
    LOGGER.debug("start reporting metrics now");

    final long timestamp = TimeUnit.MILLISECONDS.toSeconds(_clock.getTime());

    for (Map.Entry<String, Counter> entry : counters_.entrySet())
    {
      reportCounter(timestamp, entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Meter> entry : meters_.entrySet())
    {
      reportMeter(timestamp, entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Timer> entry : timers_.entrySet())
    {
      reportTimer(timestamp, entry.getKey(), entry.getValue());
    }
  }

  private void report(final DataPoint dataPoint_, final double rate_)
  {
    _dao.insert(dataPoint_, rate_);
  }

  private void report(final DataPoint dataPoint_)
  {
    _dao.insert(dataPoint_);
  }

  private void reportCounter(final long timestamp_, final String name_, final Counter counter_)
  {
    report(new DataPoint(timestamp_, name_, counter_.getCount()));
  }

  private void reportMeter(final long timestamp_, final String name_, final Meter meter_)
  {
    report(new DataPoint(timestamp_,
                         name_,
                         meter_.getCount()),
           convertRate(meter_.getOneMinuteRate()));
  }

  private void reportTimer(final long timestamp_, final String name_, final Timer timer_)
  {
    report(new DataPoint(timestamp_,
                         name_,
                         timer_.getCount()),
           convertRate(timer_.getOneMinuteRate()));
  }

  /**
   * A builder for {@link CsvReporter} instances. Defaults to using the default locale, converting
   * rates to events/second, converting durations to milliseconds, and not filtering metrics.
   */
  public static class Builder
  {
    private final MetricRegistry _registry;
    private Clock _clock;
    private TimeUnit _durationUnit;
    private MetricFilter _filter;
    private TimeUnit _rateUnit;

    private Builder(MetricRegistry registry)
    {
      _registry = registry;
      _rateUnit = TimeUnit.SECONDS;
      _durationUnit = TimeUnit.MILLISECONDS;
      _clock = Clock.defaultClock();
      _filter = MetricFilter.ALL;
    }

    /**
     * Builds a {@link CsvReporter} with the given properties, writing {@code .csv} files to the
     * given directory.
     *
     * @param dao_ the dao interface to persist the metrics to a database
     * @return a {@link CsvReporter}
     */
    public DbReporter build(final MetricsDAO dao_)
    {
      return new DbReporter(_registry,
                            dao_,
                            _rateUnit,
                            _durationUnit,
                            _clock,
                            _filter);
    }

    /**
     * Convert durations to the given time unit.
     *
     * @param durationUnit a unit of time
     * @return {@code this}
     */
    public Builder convertDurationsTo(TimeUnit durationUnit)
    {
      _durationUnit = durationUnit;
      return this;
    }

    /**
     * Convert rates to the given time unit.
     *
     * @param rateUnit a unit of time
     * @return {@code this}
     */
    public Builder convertRatesTo(TimeUnit rateUnit)
    {
      _rateUnit = rateUnit;
      return this;
    }

    /**
     * Only report metrics which match the given filter.
     *
     * @param filter a {@link MetricFilter}
     * @return {@code this}
     */
    public Builder filter(MetricFilter filter)
    {
      _filter = filter;
      return this;
    }
  }
}
