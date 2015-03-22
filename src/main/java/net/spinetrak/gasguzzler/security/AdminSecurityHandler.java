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

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

import java.io.IOException;

public class AdminSecurityHandler extends ConstraintSecurityHandler
{
  private static final String ADMIN_ROLE = "admin";

  public AdminSecurityHandler(final String userName_, final String password_)
  {
    final Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, ADMIN_ROLE);
    constraint.setAuthenticate(true);
    constraint.setRoles(new String[]{ADMIN_ROLE});
    final ConstraintMapping cm = new ConstraintMapping();
    cm.setConstraint(constraint);
    cm.setPathSpec("/*");
    setAuthenticator(new BasicAuthenticator());
    addConstraintMapping(cm);
    setLoginService(new AdminMappedLoginService(userName_, password_, ADMIN_ROLE));
  }

  private class AdminMappedLoginService extends MappedLoginService
  {
    public AdminMappedLoginService(final String userName_, final String password_, final String role_)
    {
      putUser(userName_, new Password(password_), new String[]{role_});
    }

    @Override
    public String getName()
    {
      return "Hello";
    }

    @Override
    protected UserIdentity loadUser(final String username_)
    {
      return null;
    }

    @Override
    protected void loadUsers() throws IOException
    {
    }
  }
}

