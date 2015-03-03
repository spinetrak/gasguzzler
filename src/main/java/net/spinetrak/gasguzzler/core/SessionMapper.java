package net.spinetrak.gasguzzler.core;

import net.spinetrak.gasguzzler.security.Session;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionMapper implements ResultSetMapper<Session>
{

  @Override
  public Session map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException
  {
    Session session = new Session();
    session.setUserid(resultSet.getInt("userid"));
    session.setToken(resultSet.getString("token"));

    return session;
  }
}
