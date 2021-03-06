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

package net.spinetrak.gasguzzler.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jcabi.manifests.Manifests;
import org.hibernate.validator.constraints.NotEmpty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildInfo
{
  final private static String BUILD_BRANCH = "Gasguzzler-Build-Branch";
  final private static String BUILD_DATE = "Gasguzzler-Build-Date";
  final private static String BUILD_NUMBER = "Gasguzzler-Build-Number";
  final private static String BUILD_URL = "Gasguzzler-Build-URL";
  final private static String BUILD_VERSION = "Gasguzzler-Build-Version";
  final private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
  private static BuildInfo _buildInfo;
  @NotEmpty
  @JsonProperty
  final private String buildBranch;
  @NotEmpty
  @JsonProperty
  final private String buildDate;
  @NotEmpty
  @JsonProperty
  final private String buildNumber;
  @NotEmpty
  @JsonProperty
  final private String buildURL;
  @NotEmpty
  @JsonProperty
  final private String buildVersion;

  private BuildInfo()
  {
    buildVersion = Manifests.exists(BUILD_VERSION) ? Manifests.read(BUILD_VERSION) : "n/a";
    buildDate = Manifests.exists(BUILD_DATE) ? getBuildDate(Manifests.read(BUILD_DATE)) : "n/a";
    buildNumber = Manifests.exists(BUILD_NUMBER) ? Manifests.read(BUILD_NUMBER) : "n/a";
    buildBranch = Manifests.exists(BUILD_BRANCH) ? Manifests.read(BUILD_BRANCH) : "n/a";
    buildURL = Manifests.exists(BUILD_URL) ? Manifests.read(BUILD_URL) : "n/a";
  }

  public static BuildInfo getInstance()
  {
    if (null == _buildInfo)
    {
      _buildInfo = new BuildInfo();
    }
    return _buildInfo;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof BuildInfo))
    {
      return false;
    }

    final BuildInfo buildInfo = (BuildInfo) o;

    if (!buildBranch.equals(buildInfo.buildBranch))
    {
      return false;
    }
    if (!buildDate.equals(buildInfo.buildDate))
    {
      return false;
    }
    if (!buildNumber.equals(buildInfo.buildNumber))
    {
      return false;
    }
    if (!buildURL.equals(buildInfo.buildURL))
    {
      return false;
    }
    return buildVersion.equals(buildInfo.buildVersion);

  }

  private String getBuildDate(final String buildDate_)
  {
    try
    {
      final Date d = new Date(Long.parseLong(buildDate_));
      return FORMATTER.format(d);
    }

    catch (final Exception ex_)
    {
      return buildDate_;
    }
  }
}
