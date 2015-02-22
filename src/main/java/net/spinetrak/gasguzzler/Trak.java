package net.spinetrak.gasguzzler;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import net.spinetrak.gasguzzler.dao.SessionDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.resources.RegistrationResource;
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
    return "spinetrak";
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
    final DBIFactory factory = new DBIFactory();
    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
    final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
    final SessionDAO sessionDAO = jdbi.onDemand(SessionDAO.class);
    environment.jersey().setUrlPattern("/api/*");
    environment.jersey().register(new UserResource(userDAO));
    environment.jersey().register(new SessionResource(userDAO,sessionDAO));
    environment.jersey().register(new RegistrationResource(userDAO,sessionDAO));
    environment.jersey().register(new SecurityProvider<>(new Authenticator(sessionDAO)));
  }
}
