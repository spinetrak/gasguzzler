package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserMapper;
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

  @SqlQuery("select * from st_user")
  @Mapper(UserMapper.class)
  List<User> findAll();

  @SqlQuery("select * from st_user where username = :username")
  @Mapper(UserMapper.class)
  User findByUsername(@Bind("username") String username);

  @SqlQuery("select * from st_user where userid = :userid")
  @Mapper(UserMapper.class)
  User findUser(@Bind("userid") int userid);

  @SqlQuery("select * from st_user where username = :username or email = :email")
  @Mapper(UserMapper.class)
  List<User> findUsersByUsernameOrEmail(@Bind("username") String username, @Bind("email") String email);

  @SqlUpdate("insert into st_user (username, password, email, role, created, updated) values (:username, :password, :email, :role, :created, :updated)")
  void insert(@Bind("username") String username, @Bind("password") String password, @Bind("email") String email,
              @Bind("role") String role, @Bind("created") Date created, @Bind("updated") Date updated);

  @SqlUpdate("update st_user set username = :username, password = :password, email = :email, updated = :updated where userid = :userid")
  void update(@Bind("username") String username, @Bind("password") String password, @Bind("email") String email,
              @Bind("updated") Date updated, @Bind("userid") int userid);
}
