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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpRedirectFilter implements Filter
{
  @Override
  public void destroy()
  {
  }

  @Override
  public void doFilter(final ServletRequest request_, final ServletResponse response_, final FilterChain chain_) throws
                                                                                                                 IOException,
                                                                                                                 ServletException
  {
    if (request_ instanceof HttpServletRequest)
    {
      final StringBuffer uri = ((HttpServletRequest) request_).getRequestURL();
      if (uri.toString().startsWith("http://"))
      {
        final String location = "https://" + uri.substring("http://".length());
        ((HttpServletResponse) response_).sendRedirect(location);
      }
      else
      {
        chain_.doFilter(request_, response_);
      }
    }
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
  }
}
