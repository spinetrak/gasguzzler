/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 spinetrak
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

package net.spinetrak.gasguzzler.security;


import com.github.toastshaman.dropwizard.auth.jwt.exceptions.TokenExpiredException;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.UserDAO;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * This is an example authenticator that takes the credentials extracted from the request by the SecurityProvider
 * and authenticates the principle
 */
public class Authenticator implements io.dropwizard.auth.Authenticator<JsonWebToken, User>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(Authenticator.class.getName());
  private byte[] secret;
  private UserDAO userDAO;
  private ExpiryValidator validator;

  public Authenticator(final UserDAO userDAO_, final byte[] secret_)
  {
    this(secret_);
    userDAO = userDAO_;
  }

  public Authenticator(final byte[] secret_)
  {
    validator = new ExpiryValidator(Duration.standardMinutes(10));
    secret = secret_;
  }

  @Override
  public Optional<User> authenticate(final JsonWebToken credentials_) throws AuthenticationException
  {
    LOGGER.info("Authenticating {}", credentials_);
    if (null == credentials_)
    {
      throw new AuthenticationException("Invalid credentials");
    }
    else
    {
      try
      {
        validator.validate(credentials_);
      }
      catch (TokenExpiredException ex_)
      {
        throw new AuthenticationException(ex_);
      }
      final User user = userDAO.select(credentials_.claim().subject());
      return Optional.fromNullable(user);
    }
  }

  public String generateJWTToken(final String username_)
  {
    return generateToken(username_, new DateTime(DateTime.now().plusWeeks(2)));
  }

  public String generateTempJWTToken(final String username_)
  {
    return generateToken(username_, new DateTime(DateTime.now().plusMinutes(30)));
  }

  public String getSecurePassword(final String password_) throws NoSuchAlgorithmException,
                                                                 InvalidKeySpecException
  {
    int iterations = 1000;
    char[] chars = password_.toCharArray();
    byte[] salt = getSalt().getBytes();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  public boolean validatePassword(final String originalPassword_, final String storedPassword_) throws
                                                                                                NoSuchAlgorithmException,
                                                                                                InvalidKeySpecException
  {
    if (originalPassword_ == null || storedPassword_ == null)
    {
      return false;
    }
    String[] parts = storedPassword_.split(":");
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = fromHex(parts[1]);
    byte[] hash = fromHex(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(originalPassword_.toCharArray(), salt, iterations, hash.length * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] testHash = skf.generateSecret(spec).getEncoded();

    int diff = hash.length ^ testHash.length;
    for (int i = 0; i < hash.length && i < testHash.length; i++)
    {
      diff |= hash[i] ^ testHash[i];
    }
    return diff == 0;
  }

  private byte[] fromHex(final String hex_) throws NoSuchAlgorithmException
  {
    byte[] bytes = new byte[hex_.length() / 2];
    for (int i = 0; i < bytes.length; i++)
    {
      bytes[i] = (byte) Integer.parseInt(hex_.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }

  private String generateToken(final String username_, final DateTime expiration_)
  {
    final HmacSHA512Signer signer = new HmacSHA512Signer(secret);
    final JsonWebToken token = JsonWebToken.builder()
      .header(JsonWebTokenHeader.HS512())
      .claim(JsonWebTokenClaim.builder()
               .subject(username_)
               .issuedAt(DateTime.now())
               .expiration(expiration_)
               .issuer("http://www.spinetrak.net")
               .notBefore(DateTime.now())
               .build())
      .build();
    final String signedToken = signer.sign(token);
    return signedToken;
  }

  private String getSalt() throws NoSuchAlgorithmException
  {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return new String(salt);
  }

  private String toHex(final byte[] array_) throws NoSuchAlgorithmException
  {
    BigInteger bi = new BigInteger(1, array_);
    String hex = bi.toString(16);
    int paddingLength = (array_.length * 2) - hex.length();
    if (paddingLength > 0)
    {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    }
    else
    {
      return hex;
    }
  }
}
