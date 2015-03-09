package net.spinetrak.gasguzzler.core;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User>
{

  @Override
  public User map(final int i_, final ResultSet resultSet_, final StatementContext statementContext_) throws
                                                                                                      SQLException
  {
    User user = new User();

    user.setUsername(resultSet_.getString("username"));
    user.setPassword(resultSet_.getString("password"));
    user.setUserid(resultSet_.getInt("userid"));
    user.setEmail(resultSet_.getString("email"));
    user.setRole(resultSet_.getString("role"));

    return user;
  }
}
