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

import net.spinetrak.gasguzzler.core.CountDataPoint;
import net.spinetrak.gasguzzler.core.DataPoint;
import net.spinetrak.gasguzzler.core.RateDataPoint;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface MetricsDAO
{
  @SqlUpdate("delete from st_metrics where m_name = :name")
  int delete(@Bind("name") final String name_);

  @SqlUpdate("delete from st_metrics where m_timestamp < :old")
  int deleteStale(@Bind("old") final long time_);

  @SqlQuery("select distinct m_name from st_metrics where m_count > 0")
  List<String> select();

  @SqlQuery("select m_timestamp, m_count from st_metrics where m_name = :name order by m_timestamp")
  @Mapper(CountMetricsMapper.class)
  List<CountDataPoint> getCount(@Bind("name") final String name_);

  @SqlQuery("select m_timestamp, m_rate from st_metrics where m_name = :name order by m_timestamp")
  @Mapper(RateMetricsMapper.class)
  List<RateDataPoint> getRate(@Bind("name") final String name_);

  @SqlUpdate("insert into st_metrics (m_timestamp, m_name, m_count, m_rate) values (:dp.timestamp, :dp.name, :dp.count, :dp.rate)")
  void insert(@BindBean("dp") final DataPoint dataPoint_);
}
