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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import net.spinetrak.gasguzzler.core.AdminUser;
import net.spinetrak.gasguzzler.core.notifications.EmailService;
import net.spinetrak.gasguzzler.metrics.DbReporter;
import net.spinetrak.gasguzzler.security.EncryptedDataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class TrakConfiguration extends Configuration
{
  @Valid
  @NotNull
  @JsonProperty
  private AdminUser admin = null;
  private Map<String, Object> daos = new HashMap<>();
  @Valid
  @NotNull
  @JsonProperty
  private EncryptedDataSourceFactory database = new EncryptedDataSourceFactory();
  @Valid
  @NotNull
  @JsonProperty
  private EmailService emailService = null;
  @Valid
  @NotNull
  @JsonProperty
  private FlywayFactory flyway = new FlywayFactory();
  @Valid
  @NotNull
  @JsonProperty
  private Boolean isHttps = false;
  private DbReporter reporter;

  public AdminUser getAdmin()
  {
    return admin;
  }

  public Object getDAO(final String key_)
  {
    return daos.get(key_);
  }

  public DataSourceFactory getDataSourceFactory()
  {
    return database;
  }

  public EmailService getEmailService()
  {
    return emailService;
  }

  public FlywayFactory getFlywayFactory()
  {
    return flyway;
  }

  public DbReporter getReporter()
  {
    return reporter;
  }

  public Boolean isHttps()
  {
    return isHttps;
  }

  protected void addDAO(final String key_, final Object dao_)
  {
    daos.put(key_, dao_);
  }

  protected void addReporter(final DbReporter reporter_)
  {
    reporter = reporter_;
  }

  private static String jwtTokenSecret = null;

  public byte[] getJwtTokenSecret() throws UnsupportedEncodingException, NoSuchAlgorithmException
  {
    return generateTokenSecret().getBytes("UTF-8");
  }

  private static String generateTokenSecret() throws NoSuchAlgorithmException
  {
    if(jwtTokenSecret != null)
    {
      return jwtTokenSecret;
    }
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return new String(salt);
  }

}
