package net.spinetrak.gasguzzler.core;

import net.spinetrak.gasguzzler.security.Session;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionMapper implements ResultSetMapper<Session>
{

  @Override
  public Session map(final int i_, final ResultSet resultSet_, final StatementContext statementContext_) throws
                                                                                                         SQLException
  {
    Session session = new Session();
    session.setUserid(resultSet_.getInt("userid"));
    session.setToken(resultSet_.getString("token"));

    return session;
  }
}
