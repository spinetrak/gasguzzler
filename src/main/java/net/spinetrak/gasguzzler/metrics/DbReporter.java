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

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class DbReporter extends ScheduledReporter
{
  private Clock _clock;
  private MetricsDAO _dao;


  private DbReporter(final MetricRegistry registry_,
                     final MetricsDAO dao_,
                     final TimeUnit rateUnit_,
                     final TimeUnit durationUnit_,
                     final Clock clock_,
                     final MetricFilter filter_)
  {
    super(registry_, "db-reporter", filter_, rateUnit_, durationUnit_);
    _dao = dao_;
    _clock = clock_;
  }

  public static Builder forRegistry(final MetricRegistry registry_)
  {
    return new Builder(registry_);
  }

  @Override
  public void report(final SortedMap<String, Gauge> gauges_, final SortedMap<String, Counter> counters_,
                     final SortedMap<String, Histogram> histograms_, final SortedMap<String, Meter> meters_,
                     final SortedMap<String, Timer> timers_)
  {
    final long timestamp = _clock.getTime();

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

    for (Map.Entry<String, Gauge> entry : gauges_.entrySet())
    {
      reportGauge(timestamp, entry.getKey(), entry.getValue());
    }
  }

  private long getGaugeCount(final Object value_)
  {
    if (null != value_)
    {
      if ((value_ instanceof Long) || (value_ instanceof Integer))
      {
        return ((Number) value_).longValue();
      }
      return 0;
    }
    return 0;
  }

  private double getGaugeRate(final Object value_)
  {
    if (null != value_)
    {
      if ((value_ instanceof Float) || (value_ instanceof Double))
      {
        return ((Number) value_).doubleValue();
      }
      return 0;
    }
    return 0;
  }

  private void report(final DataPoint dataPoint_)
  {
    final long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(45, TimeUnit.DAYS);
    _dao.deleteStale(cutoff);
    _dao.insert(dataPoint_);
  }

  private void reportCounter(final long timestamp_, final String name_, final Counter counter_)
  {
    final DataPoint dp = new DataPoint();
    dp.setTimestamp(timestamp_);
    dp.setRate(-1);
    dp.setName(name_);
    dp.setCount(counter_.getCount());
    report(dp);
  }

  private void reportGauge(final long timestamp_, final String name_, final Gauge gauge_)
  {
    final DataPoint dp = new DataPoint();
    dp.setTimestamp(timestamp_);
    dp.setRate(getGaugeRate(gauge_.getValue()));
    dp.setName(name_);
    dp.setCount(getGaugeCount(gauge_.getValue()));
    report(dp);
  }

  private void reportMeter(final long timestamp_, final String name_, final Meter meter_)
  {
    final DataPoint dp = new DataPoint();
    dp.setTimestamp(timestamp_);
    dp.setRate(convertRate(meter_.getOneMinuteRate()));
    dp.setName(name_);
    dp.setCount(meter_.getCount());
    report(dp);
  }

  private void reportTimer(final long timestamp_, final String name_, final Timer timer_)
  {
    final DataPoint dp = new DataPoint();
    dp.setTimestamp(timestamp_);
    dp.setRate(convertRate(timer_.getOneMinuteRate()));
    dp.setName(name_);
    dp.setCount(timer_.getCount());
    report(dp);
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

    public DbReporter build(final MetricsDAO dao_)
    {
      return new DbReporter(_registry,
                            dao_,
                            _rateUnit,
                            _durationUnit,
                            _clock,
                            _filter);
    }

    public Builder convertDurationsTo(final TimeUnit durationUnit_)
    {
      _durationUnit = durationUnit_;
      return this;
    }

    public Builder convertRatesTo(final TimeUnit rateUnit_)
    {
      _rateUnit = rateUnit_;
      return this;
    }


    public Builder filter(final MetricFilter filter_)
    {
      _filter = filter_;
      return this;
    }
  }
}
