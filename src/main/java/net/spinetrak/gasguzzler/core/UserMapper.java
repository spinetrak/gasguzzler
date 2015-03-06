package net.spinetrak.gasguzzler.core;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User>
{

  @Override
  public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException
  {
    User user = new User();

    user.setUsername(resultSet.getString("username"));
    user.setPassword(resultSet.getString("password"));
    user.setUserid(resultSet.getInt("userid"));
    user.setEmail(resultSet.getString("email"));
    user.setRole(resultSet.getString("role"));

    return user;
  }
}
