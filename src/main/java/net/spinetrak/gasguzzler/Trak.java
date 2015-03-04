package net.spinetrak.gasguzzler;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.resources.SessionResource;
import net.spinetrak.gasguzzler.resources.UserResource;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.SecurityProvider;
import org.skife.jdbi.v2.DBI;

/**
 * Created by spinetrak on 14/02/15.
 */
public class Trak extends Application<TrakConfiguration>
{
  public Trak()
  {

  }

  public static void main(String[] args) throws Exception
  {
    new Trak().run(args);
  }

  @Override
  public String getName()
  {
    return "gasguzzler";
  }

  @Override
  public void initialize(Bootstrap<TrakConfiguration> bootstrap)
  {
    bootstrap.addBundle(new ViewBundle());
    bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "static"));
    bootstrap.addBundle(new AssetsBundle("/assets/app", "/app", null, "app"));
    bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
    bootstrap.addBundle(new AssetsBundle("/assets/images", "/images", null, "images"));
    bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
  }

  @Override
  public void run(TrakConfiguration configuration,
                  Environment environment) throws ClassNotFoundException
  {
    final DBI jdbi = new DBIFactory().build(environment, configuration.getDataSourceFactory(), "postgres");
    final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
    final SessionDAO sessionDAO = jdbi.onDemand(SessionDAO.class);

    configuration.addDAO("userDAO", userDAO);
    configuration.addDAO("sessionDAO", sessionDAO);
    environment.jersey().register(sessionDAO);
    environment.jersey().setUrlPattern("/api/*");
    environment.jersey().register(new UserResource(userDAO, sessionDAO));
    environment.jersey().register(new SessionResource(userDAO, sessionDAO));
    environment.jersey().register(new SecurityProvider<>(new Authenticator(sessionDAO)));
  }
}
