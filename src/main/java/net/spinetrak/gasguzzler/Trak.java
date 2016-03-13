/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 spinetrak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.spinetrak.gasguzzler;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.meltmedia.dropwizard.crypto.CryptoBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.spinetrak.gasguzzler.admin.UserHealthCheck;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.MetricsDAO;
import net.spinetrak.gasguzzler.dao.UserDAO;
import net.spinetrak.gasguzzler.jobs.EmailNotificationJob;
import net.spinetrak.gasguzzler.metrics.DbReporter;
import net.spinetrak.gasguzzler.resources.BuildInfoResource;
import net.spinetrak.gasguzzler.resources.MetricsResource;
import net.spinetrak.gasguzzler.resources.SessionResource;
import net.spinetrak.gasguzzler.resources.UserResource;
import net.spinetrak.gasguzzler.security.AdminSecurityHandler;
import net.spinetrak.gasguzzler.security.Authenticator;
import net.spinetrak.gasguzzler.security.Authorizer;
import net.spinetrak.gasguzzler.security.HttpRedirectFilter;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

import javax.servlet.DispatcherType;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class Trak extends Application<TrakConfiguration>
{
  public static void main(final String[] args_) throws Exception
  {
    new Trak().run(args_);
  }

  @Override
  public String getName()
  {
    return "gasguzzler";
  }

  @Override
  public void initialize(final Bootstrap<TrakConfiguration> bootstrap_)
  {
    bootstrap_.addBundle(new AssetsBundle("/assets", "/", "index.html", "static"));
    bootstrap_.addBundle(new AssetsBundle("/assets/app", "/app", null, "app"));
    bootstrap_.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
    bootstrap_.addBundle(new AssetsBundle("/assets/images", "/images", null, "images"));
    bootstrap_.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));

    bootstrap_.addBundle(CryptoBundle.builder().build());

    bootstrap_.addBundle(new DBIExceptionsBundle());
    bootstrap_.addBundle(new FlywayBundle<TrakConfiguration>()
    {
      @Override
      public DataSourceFactory getDataSourceFactory(final TrakConfiguration configuration_)
      {
        return configuration_.getDataSourceFactory();
      }

      @Override
      public FlywayFactory getFlywayFactory(final TrakConfiguration configuration_)
      {
        return configuration_.getFlywayFactory();
      }
    });
  }

  @Override
  public void run(final TrakConfiguration configuration_,
                  final Environment environment_) throws ClassNotFoundException, UnsupportedEncodingException,
                                                         NoSuchAlgorithmException
  {

    final Flyway flyway = configuration_.getFlywayFactory().build(
      configuration_.getDataSourceFactory().build(environment_.metrics(), "flyway"));
    flyway.repair();
    flyway.migrate();

    final DBI jdbi = new DBIFactory().build(environment_, configuration_.getDataSourceFactory(), "postgres");

    final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
    final MetricsDAO metricsDAO = jdbi.onDemand(MetricsDAO.class);

    configuration_.addDAO("userDAO", userDAO);
    configuration_.addDAO("metricsDAO", metricsDAO);

    environment_.lifecycle().scheduledExecutorService("emailNotificationJob").build().scheduleAtFixedRate(
      new EmailNotificationJob(configuration_.getEmailService()), 0,
      1,
      TimeUnit.MINUTES);


    final byte[] secret =                                      configuration_.getJwtTokenSecret();
    final Authenticator authenticator =  new Authenticator(userDAO,secret);

    environment_.jersey().register(new BuildInfoResource());
    environment_.jersey().register(new UserResource(userDAO, authenticator, configuration_.getAdmin().getEmail()));
    environment_.jersey().register(new SessionResource(userDAO, authenticator));
    environment_.jersey().register(new MetricsResource(metricsDAO));

    environment_.jersey().register(new AuthDynamicFeature(
      new JWTAuthFilter.Builder<User>()
        .setTokenParser(new DefaultJsonWebTokenParser())
        .setTokenVerifier(new HmacSHA512Verifier(secret))
        .setRealm("realm")
        .setPrefix("Bearer")
        .setAuthenticator(authenticator)
        .setAuthorizer(new Authorizer())
        .buildAuthFilter()));
    environment_.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    environment_.jersey().register(RolesAllowedDynamicFeature.class);


    if (configuration_.isHttps())
    {
      environment_.servlets().addFilter("HttpRedirectFilter", new HttpRedirectFilter())
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
    environment_.healthChecks().register("users", new UserHealthCheck(userDAO));

    environment_.admin().setSecurityHandler(
      new AdminSecurityHandler(configuration_.getAdmin().getUsername(), configuration_.getAdmin().getPassword()));

    final DbReporter reporter = DbReporter.forRegistry(environment_.metrics())
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build(metricsDAO);
    reporter.start(60, TimeUnit.SECONDS);
    configuration_.addReporter(reporter);
  }
}
