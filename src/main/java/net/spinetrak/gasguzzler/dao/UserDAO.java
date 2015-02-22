package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface UserDAO
{

  @SqlUpdate("delete from st_user where userid = :userid")
  void delete(
    @Bind("userid") int userid
  );

  @SqlQuery("select * from st_user")
  @Mapper(UserMapper.class)
  List<User> findAll();

  @SqlQuery("select * from st_user where userid = :userid limit 1")
  @Mapper(UserMapper.class)
  User findUser(
    @Bind("userid") int userid
  );

  @SqlQuery("select * from st_user where username = :username and password = :password limit 1 ")
  @Mapper(UserMapper.class)
  User findUserByUsernameAndPassword(
    @Bind("username") String username
    , @Bind("password") String password
  );

  @SqlQuery("select * from st_user where username = :username or email = :email")
  @Mapper(UserMapper.class)
  List<User> findUsersByUsernameOrEmail(
    @Bind("username") String username
    , @Bind("email") String email
  );

  @SqlQuery("select salt from st_user where username = :username")
  String getSalt(
    @Bind("username") String username
  );

  @SqlUpdate("insert into st_user (username, password, email, role, salt) values (:username, :password, :email, :role, :salt)")
  void insert(
    @Bind("username") String username
    , @Bind("password") String password
    , @Bind("email") String email,
    @Bind("role") String role,
    @Bind("salt") String salt
  );

  @SqlUpdate("update st_user set username = :username, email = :email where userid = :userid")
  void update(
    @Bind("username") String username
    , @Bind("email") String email,
    @Bind("userid") int userid
  );
}
