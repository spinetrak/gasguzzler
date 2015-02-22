package net.spinetrak.gasguzzler.security;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.dao.SessionDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This is an example authenticator that takes the credentials extracted from the request by the SecurityProvider
 * and authenticates the principle
 */
public class Authenticator implements io.dropwizard.auth.Authenticator<Credentials, User>
{

  private SessionDAO sessionDAO;

  public Authenticator(SessionDAO sessionDAO_)
  {
    sessionDAO = sessionDAO_;
  }

  public Authenticator()
  {
  }

  public static String getSalt() throws NoSuchAlgorithmException
  {
    //Always use a SecureRandom generator
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    //Create array for salt
    byte[] salt = new byte[48];
    //Get a random salt
    sr.nextBytes(salt);
    //return salt
    return salt.toString();

  }

  public static String getSecurePassword(String passwordToHash, String salt)
  {
    String generatedPassword = null;
    try
    {
      // Create MessageDigest instance for MD5
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      //Add password bytes to digest
      md.update(salt.getBytes());
      //Get the hash's bytes
      byte[] bytes = md.digest(passwordToHash.getBytes());
      //This bytes[] has bytes in decimal format;
      //Convert it to hexadecimal format
      StringBuilder sb = new StringBuilder();
      for (final byte aByte : bytes)
      {
        sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
      }
      //Get complete hashed password in hex format
      generatedPassword = sb.toString();
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    return generatedPassword;
  }

  @Override
  public Optional<User> authenticate(Credentials credentials) throws AuthenticationException
  {
    if (null == sessionDAO.findSession(credentials.getUserid(), credentials.getToken()))
    {
      throw new AuthenticationException("Invalid credentials");
    }
    else
    {
      final User user = new User();
      user.setUserid(credentials.getUserid());
      user.setToken(credentials.getToken());
      user.setRole(credentials.getToken().contains("Admin") ? User.ROLE_ADMIN : User.ROLE_USER);

      return Optional.fromNullable(user);
    }
  }
}
