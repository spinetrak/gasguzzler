package net.spinetrak.gasguzzler;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by spinetrak on 14/02/15.
 */
public class TrakConfiguration extends Configuration
{
  @Valid
  @NotNull
  @JsonProperty
  private DataSourceFactory database = new DataSourceFactory();

  @Valid
  @NotNull
  @JsonProperty
  private FlywayFactory flyway = new FlywayFactory();

  private Map<String, Object> _daos = new HashMap<>();

  public Object getDAO(final String key_)
  {
    return _daos.get(key_);
  }
  
  public DataSourceFactory getDataSourceFactory()
  {
    return database;
  }

  public FlywayFactory getFlywayFactory()
  {
    return flyway;
  }

  protected void addDAO(final String key_, final Object dao_)
  {
    _daos.put(key_, dao_);
  }
}
