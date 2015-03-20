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

package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.security.Session;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface SessionDAO
{
  @SqlUpdate("delete from st_session where created < :date")
  int delete(@Bind("date") final Date date_);
  
  @SqlUpdate("delete from st_session where userid = :s.userid and token = :s.token")
  int delete(@BindBean("s") final Session session_);

  @SqlUpdate("delete from st_session where userid = :u.userid")
  int delete(@BindBean("u") final User user_);

  @SqlUpdate("insert into st_session (userid, token, created) values (:s.userid, :s.token, :s.date)")
  int insert(@BindBean("s") final Session session_);

  @SqlQuery("select userid, token from st_session where userid = :s.userid and token = :s.token limit 1")
  @Mapper(SessionMapper.class)
  Session select(@BindBean("s") final Session session_);

  @SqlQuery("select * from st_session")
  @Mapper(SessionMapper.class)
  List<Session> select();
}
