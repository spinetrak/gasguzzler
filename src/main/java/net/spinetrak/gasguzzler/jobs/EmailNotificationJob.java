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

import net.spinetrak.gasguzzler.core.notifications.EmailNotification;
import net.spinetrak.gasguzzler.core.notifications.EmailQueue;
import net.spinetrak.gasguzzler.core.notifications.EmailService;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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

    final Client client = ClientBuilder.newClient();
    final WebTarget target = client.target(_emailService.endpoint());

    target.register(HttpAuthenticationFeature.basic("api", _emailService.key()));

    final MultivaluedMap data = email_.format();
    data.add("from", _emailService.from());

    final Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).
      post(Entity.form(data));

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
