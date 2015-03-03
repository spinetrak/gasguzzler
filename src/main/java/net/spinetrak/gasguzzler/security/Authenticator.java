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
public class Authenticator implements io.dropwizard.auth.Authenticator<Session, User>
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
    return new String(salt);
  }

  public static String getSecurePassword(String passwordToHash, String salt) throws NoSuchAlgorithmException
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
    return sb.toString();
  }

  @Override
  public Optional<User> authenticate(Session session_) throws AuthenticationException
  {
    if ((null == session_) || (null == sessionDAO) || (null == sessionDAO.findSession(session_.getUserid(),
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
