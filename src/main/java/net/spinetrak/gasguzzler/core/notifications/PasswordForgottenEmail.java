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

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class PasswordForgottenEmail extends EmailNotification
{
  private final String _token;
  private final int _userid;

  public PasswordForgottenEmail(final String email_, int userid_, final String token_)
  {
    to(email_);
    _token = token_;
    _userid = userid_;
  }

  @Override
  public MultivaluedMap format()
  {
    final MultivaluedMap data = new MultivaluedHashMap();
    data.add("to", to());
    data.add("subject", "Gasguzzler Request");
    data.add("html", getBodyHTML());

    return data;
  }

  public String toString()
  {
    return to();
  }

  @Override
  final protected String getBodyHTML()
  {
    return "<p>The Gasguzzler has received a request a request to reset your password.</p>" +
      "<p>You may safely ignore this message, if you did not make this request.</p>" +
      "<p>If you did make this request, please click <a href='" + getPasswordResetLink() + "'>here</a> to pick a new password.</p>" +
      "<p>Please click on this link within 30 minutes, as it will expire.</p>";
  }

  final protected String getPasswordResetLink()
  {
    return getEmailService().root() + "/#user?t=" + _token + "&i=" + _userid;
  }
}
