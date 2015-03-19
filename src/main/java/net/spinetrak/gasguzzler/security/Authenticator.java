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

package net.spinetrak.gasguzzler.security;


import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;
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
public class Authenticator implements io.dropwizard.auth.Authenticator<Session, User>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(Authenticator.class.getName());
  
  private SessionDAO sessionDAO;

  public Authenticator(SessionDAO sessionDAO_)
  {
    sessionDAO = sessionDAO_;
  }

  public Authenticator()
  {
  }

  public static String getSecurePassword(String password) throws NoSuchAlgorithmException,
                                                                 InvalidKeySpecException
  {
    int iterations = 1000;
    char[] chars = password.toCharArray();
    byte[] salt = getSalt().getBytes();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  public static boolean validatePassword(String originalPassword, String storedPassword) throws
                                                                                         NoSuchAlgorithmException,
                                                                                         InvalidKeySpecException
  {
    String[] parts = storedPassword.split(":");
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = fromHex(parts[1]);
    byte[] hash = fromHex(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] testHash = skf.generateSecret(spec).getEncoded();

    int diff = hash.length ^ testHash.length;
    for (int i = 0; i < hash.length && i < testHash.length; i++)
    {
      diff |= hash[i] ^ testHash[i];
    }
    return diff == 0;
  }

  private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
  {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++)
    {
      bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }

  private static String getSalt() throws NoSuchAlgorithmException
  {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return new String(salt);
  }

  private static String toHex(byte[] array) throws NoSuchAlgorithmException
  {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0)
    {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    }
    else
    {
      return hex;
    }
  }

  @Override
  public Optional<User> authenticate(final Session session_) throws AuthenticationException
  {
    LOGGER.info("Authenticating {}", session_);
    if ((null == session_) || (null == sessionDAO) || (null == sessionDAO.select(session_.getUserid(),
                                                                                 session_.getToken())))
    {
      throw new AuthenticationException("Invalid credentials");
    }
    else
    {
      final User user = new User();
      user.setUserid(session_.getUserid());
      user.setToken(session_.getToken());
      user.setRole(session_.getToken().contains("Admin") ? User.ROLE_ADMIN : User.ROLE_USER);

      return Optional.fromNullable(user);
    }
  }
}
