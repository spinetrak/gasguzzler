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
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

public interface UserDAO
{

  @SqlUpdate("delete from st_user where userid = :userid")
  void delete(@Bind("userid") int userid);

  @SqlUpdate("insert into st_user (username, password, email, role, created, updated) values (:username, :password, :email, :role, :created, :updated)")
  void insert(@Bind("username") String username, @Bind("password") String password, @Bind("email") String email,
              @Bind("role") String role, @Bind("created") Date created, @Bind("updated") Date updated);

  @SqlQuery("select * from st_user where username = :username")
  @Mapper(UserMapper.class)
  User select(@Bind("username") String username);

  @SqlQuery("select * from st_user where userid = :userid")
  @Mapper(UserMapper.class)
  User select(@Bind("userid") int userid);

  @SqlQuery("select * from st_user where username = :username or email = :email")
  @Mapper(UserMapper.class)
  List<User> select(@Bind("username") String username, @Bind("email") String email);

  @SqlQuery("select * from st_user")
  @Mapper(UserMapper.class)
  List<User> select();

  @SqlUpdate("update st_user set username = :username, password = :password, email = :email, updated = :updated where userid = :userid")
  void update(@Bind("username") String username, @Bind("password") String password, @Bind("email") String email,
              @Bind("updated") Date updated, @Bind("userid") int userid);
}
