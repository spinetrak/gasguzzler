package net.spinetrak.gasguzzler.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SessionDAO
{
  @SqlUpdate("insert into st_session (userid, token, created) values (:userid, :token, :created)")
  void insert(
    @Bind("userid") int userid,
    @Bind("token") String token
    , @Bind("created") java.util.Date created
  );

  @SqlQuery("select token from st_session where userid = :userid and token = :token limit 1")
  String findSession(
    @Bind("userid") int userid, @Bind("token") String token
  );

  @SqlUpdate("delete from st_session where userid = :userid and token = :token")
  void delete(
    @Bind("userid") int userid, @Bind("token") String token
  );
}
