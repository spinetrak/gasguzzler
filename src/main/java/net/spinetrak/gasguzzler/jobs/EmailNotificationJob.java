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

package net.spinetrak.gasguzzler.jobs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.spinetrak.gasguzzler.core.notifications.EmailNotification;
import net.spinetrak.gasguzzler.core.notifications.EmailQueue;
import net.spinetrak.gasguzzler.core.notifications.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.Queue;

public class EmailNotificationJob implements Runnable
{
  private final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationJob.class);

  private final EmailService _emailService;

  public EmailNotificationJob(final EmailService emailService_)
  {
    _emailService = emailService_;
  }

  @Override
  public void run()
  {
    final Queue<EmailNotification> queue = new EmailQueue().getQueue();
    LOGGER.info("Processing {} email notifications", queue);
    process(queue);
  }

  private void process(final Queue<EmailNotification> queue_)
  {
    while (!queue_.isEmpty())
    {
      process(queue_.poll());
    }
  }

  private void process(final EmailNotification email_)
  {
    if (null == email_)
    {
      return;
    }
    email_.setEmailService(_emailService);
    LOGGER.info("Sending " + email_);

    final Client client = Client.create();
    client.addFilter(new HTTPBasicAuthFilter("api", _emailService.key()));
    final WebResource webResource =
      client.resource(_emailService.endpoint());
    final MultivaluedMapImpl data = email_.format();
    data.add("from", _emailService.from());

    final ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
      post(ClientResponse.class, data);

    if (200 == response.getStatusInfo().getStatusCode())
    {
      LOGGER.info("Message sent successfully!");
    }
    else
    {
      LOGGER.error("Error sending message: " + response.toString());
    }
  }
}
