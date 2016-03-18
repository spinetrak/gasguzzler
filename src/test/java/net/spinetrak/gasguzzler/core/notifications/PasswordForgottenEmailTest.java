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

package net.spinetrak.gasguzzler.core.notifications;

import net.spinetrak.gasguzzler.core.User;
import net.spinetrak.gasguzzler.core.UserTest;
import net.spinetrak.gasguzzler.security.Authenticator;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.*;

public class PasswordForgottenEmailTest
{
  @Test
  public void testPasswordForgottenEmail()
  {
    try
    {
      final Authenticator authenticator = new Authenticator("secret".getBytes("UTF-8"));
      final User user = UserTest.getUser();
      final PasswordForgottenEmail email = new PasswordForgottenEmail("foo@bar.net", user.getUserid(),
                                                                      authenticator.generateTempJWTToken(user.getUsername()));
      email.setEmailService(new EmailService());
      assertNotNull(email);
      assertEquals("foo@bar.net", email.to());
      assertEquals("foo@bar.net", email.toString());

      final MultivaluedMap map = email.format();
      assertNotNull(map);
      assertEquals("[foo@bar.net]", map.get("to").toString());
    }
    catch (Exception ex_)
    {
      fail(ex_.getMessage());
    }
  }
}
