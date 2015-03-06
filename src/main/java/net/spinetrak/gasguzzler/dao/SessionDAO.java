package net.spinetrak.gasguzzler.dao;

import net.spinetrak.gasguzzler.core.SessionMapper;
import net.spinetrak.gasguzzler.security.Session;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface SessionDAO
{
  @SqlUpdate("delete from st_session where userid = :userid and token = :token")
  void delete(@Bind("userid") int userid, @Bind("token") String token);

  @SqlUpdate("delete from st_session where userid = :userid")
  void delete(@Bind("userid") int userid);

  @SqlQuery("select userid, token from st_session where userid = :userid and token = :token limit 1")
  @Mapper(SessionMapper.class)
  Session findSession(@Bind("userid") int userid, @Bind("token") String token);

  @SqlUpdate("insert into st_session (userid, token, created) values (:userid, :token, :created)")
  void insert(@Bind("userid") int userid, @Bind("token") String token, @Bind("created") java.util.Date created);
}
