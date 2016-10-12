/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.soleng.bdsplugin;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import com.blackducksoftware.integration.phone.home.PhoneHomeClient;
import com.blackducksoftware.integration.phone.home.enums.BlackDuckName;
import com.blackducksoftware.integration.phone.home.enums.ThirdPartyName;
import com.blackducksoftware.integration.util.ResourceUtil;
import com.blackducksoftware.soleng.bdsplugin.dao.ProtexDAO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicenseCategory;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;

/**
 *
 * Simple class to connect to Protex using the SDK Should use the DAO
 *
 */
public class ProtexConnector {

	private final Logger log = LoggerFactory.getLogger(ProtexConnector.class);
	private ProtexDAO pDAO = null;

	public ProtexConnector(final Settings settings, final Project sonarProject) throws Exception {
		pDAO = new ProtexDAO(settings, sonarProject);
	}

	public ApplicationPOJO populateApplicationWithProtexData(final ApplicationPOJO pojo,
			final SensorContext sensorContext) {
		try {
			pDAO.populateProjectInfo(pojo);
			// phone-home
			try {
				String protexHost = null;
				String protexServerVersion = null;
				try {
					final URL protexUrl = pDAO.getProtexServerUrl();
					protexHost = protexUrl.getHost();
				} catch (final Exception e) {
					log.debug("Could not get the Protex Host name.");
				}
				protexServerVersion = pDAO.getProtexServerVersion();
				bdPhoneHome(protexHost, protexServerVersion);
			} catch (final Exception e) {
				log.debug("Unable to phone-home", e);
			}
			pDAO.populateProjectFileCounts(pojo, sensorContext);

			log.info("App information: " + pojo.toString());
		} catch (final Exception e) {
			log.error("Unable to populate Protex data", e);
			pojo.setProtexErrorMsg(e.getMessage());
		} catch (final Throwable t) {
			pojo.setProtexErrorMsg(t.getMessage());
			log.error("Fatal Protex error: Unable to generate Protex information", t);
		}

		return pojo;
	}

	/**
	 * Uses the DAO to populate the license pojo with: license reach level,
	 * license attribute name
	 *
	 * Then sorts the license objects based on name/frequency/number
	 *
	 * @param pojo
	 * @return
	 */
	public ApplicationPOJO populateLicenseData(final ApplicationPOJO pojo) {
		for (final LicensePOJO license : pojo.getLicenses()) {
			pDAO.populateLicenseAttributes(license);
		}

		sortLicenses(pojo);

		return pojo;
	}

	/**
	 * Iterates through all the license pojos and determines their frequency and
	 * how should they be sorted.
	 *
	 * TODO: Extract these rules into a config (even though it will seldom
	 * change0 TODO: We have to use the unknown name instead of relying on
	 * number, due to the limitation of Protex SDK (New SDK fixes promised to be
	 * delivered soon) TODO: This will change in 7.0 with all these categories
	 * being baked in. Name Unknown: Unknown Number < 2: Permissive Number >=2
	 * && < 5: Weak Reciprocal Number >= 5: Reciprocal
	 *
	 * @param pojo
	 */
	private void sortLicenses(final ApplicationPOJO pojo) {
		final HashMap<String, LicenseCategory> sortedLicenseMap = new HashMap<>();
		final List<LicensePOJO> licenses = pojo.getLicenses();

		for (final LicensePOJO license : licenses) {
			final Integer reachNumber = license.getLicenseReachNumber();
			if (reachNumber == null) {
				log.warn("No reach number for license id (defaulting to unknown): " + license.getLicenseID());
				license.setLicenseID("unknown");
			}

			if (license.getLicenseID().equals("unknown")) {
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_UNKNOWN, "gray");
			} else if (reachNumber < 2) {
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_PERMISSIVE, "green");
			} else if (reachNumber >= 2 && reachNumber < 5) {
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_WEAK_RECIPROCAL, "orange");
			} else if (reachNumber >= 5) {
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_RECIPROCAL, "red");
			}

		}

		pojo.setSortedLicenseMap(sortedLicenseMap);
	}

	private void populateMap(final HashMap<String, LicenseCategory> sortedLicenseMap, final String key,
			final String color) {
		final LicenseCategory category = sortedLicenseMap.get(key);
		if (category == null) {
			final LicenseCategory cat = new LicenseCategory(key, color);
			cat.incrementCount();
			sortedLicenseMap.put(key, cat);
			log.debug("New license category entry for: " + key);
		} else {
			category.incrementCount();
			log.debug("Adding existing license category for key: " + key + " count: " + category.getCount());
		}
	}

	/**
	 * @param protexHostName
	 *            Host name of the protex instance that this plugin uses
	 * @param protexVersion
	 *            Protex server version
	 *
	 *            This method "phones-home" to the internal BlackDuck
	 *            Integrations server. Every time a build is kicked off,
	 */
	public void bdPhoneHome(final String protexHostName, final String protexVersion) throws Exception {
		if (StringUtils.isNotBlank(protexHostName)) {
			final String thirdPartyVersion = ResourceUtil.getResourceAsString(Sensor.class, "sq-version.txt",
					StandardCharsets.UTF_8);
			final String pluginVersion = ResourceUtil.getResourceAsString(getClass(), "bd-plugin-version.txt",
					StandardCharsets.UTF_8);
			if (StringUtils.isNotBlank(thirdPartyVersion) && StringUtils.isNotBlank(pluginVersion)) {
				final PhoneHomeClient phClient = new PhoneHomeClient();
				phClient.callHomeIntegrations(null, protexHostName.trim(), BlackDuckName.PROTEX, protexVersion.trim(),
						ThirdPartyName.SONARQUBE,
						thirdPartyVersion.trim(), pluginVersion.trim());
			}
		}
	}

}
