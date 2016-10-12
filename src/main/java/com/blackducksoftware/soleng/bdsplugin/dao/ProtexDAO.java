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
package com.blackducksoftware.soleng.bdsplugin.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.license.LicenseAttributes;
import com.blackducksoftware.sdk.protex.license.LicenseExtensionLevel;
import com.blackducksoftware.sdk.protex.policy.PolicyApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.NodeCountType;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginProtexConfigManager;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginUser;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.BDSProtexPojo;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;
import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.CodeTreeHelper;
import com.blackducksoftware.tools.connector.protex.ProtexServerWrapper;

public class ProtexDAO extends CommonDAO {
	private final Logger log = LoggerFactory.getLogger(ProtexDAO.class.getName());

	private LicenseApi licenseApi = null;
	private PolicyApi policyApi = null;
	private Settings settings = null;
	private BDSPluginProtexConfigManager configManager = null;
	private ProtexServerWrapper<?> protexWrapper = null;
	private String protexUrl = null;

	public ProtexDAO(final Settings settings, final org.sonar.api.resources.Project sonarProject) throws Exception {
		this.settings = settings;
		authenticate();
	}

	@Override
	public void authenticate() throws Exception {
		try {
			/**
			 * First we check to see if project settings contain data, if not
			 * use global.
			 */
			// Map<String, String> props = settings.getProperties();

			final String SERVER = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_URL);
			protexUrl = SERVER;
			final String USER_NAME = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_USERNAME);
			final String PASSWORD = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD);
			final String PROJECT_NAME = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_PROJECT);

			final BDSPluginUser user = new BDSPluginUser(SERVER, USER_NAME, PASSWORD);

			configManager = new BDSPluginProtexConfigManager(user);
			configManager.setProtexPojectName(PROJECT_NAME);

			configManager = (BDSPluginProtexConfigManager) collectGeneralSettings(configManager, settings);

			// workaround for this here
			// http://fusesource.com/forums/thread.jspa?messageID=10988
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

			// AK -- Use the deprecated method for now
			protexWrapper = new ProtexServerWrapper(configManager.getServerBean(APPLICATION.PROTEX), configManager,
					false);

			licenseApi = protexWrapper.getInternalApiWrapper().getLicenseApi();
			policyApi = protexWrapper.getInternalApiWrapper().getPolicyApi();

			log.info("Protex authentication completed");
		} catch (final Exception e) {
			throw new Exception("Authentication failure: " + e.getMessage());
		}

	}

	public URL getProtexServerUrl() throws MalformedURLException {
		return new URL(protexUrl);
	}

	public String getProtexServerVersion() throws SdkFault {
		return policyApi.getSystemInformation().getBdsClientVersion();
	}

	public void populateProjectInfo(final ApplicationPOJO sonarQubeApplication) {
		String projectName = null;
		try {
			// Use the user specified project
			projectName = configManager.getProtexPojectName();
			if (projectName == null || projectName.length() == 0) {
				// If it is empty, then take it from the Config Manager, Code
				// Center may have populated it.
				projectName = sonarQubeApplication.getProjectName();
			}

			// If it is still empty, then there is no sense in continuing.
			if (projectName == null || projectName.length() == 0) {
				throw new Exception("Nothing to analyze, project name is empty");
			}

			log.info("Getting project information for project name: " + projectName);

			// Get the project from the SDK, if the project has a bad name,
			// missing, etc it will bomb.
			// workaround for this here
			// http://fusesource.com/forums/thread.jspa?messageID=10988
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

			final ProtexProjectPojo projectPojo = (ProtexProjectPojo) protexWrapper.getProjectByName(projectName);

			sonarQubeApplication.setProjectID(projectPojo.getProjectKey());
			sonarQubeApplication.setProjectName(projectPojo.getProjectName());
			sonarQubeApplication.setDateLastAnalyzed(projectPojo.getAnalyzedDate());

			// Grab the url
			final String serverURL = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_URL);

			// TODO: Figure it out, this hard-coded stuff is terrible.
			final String protexBomURL = serverURL + (serverURL.endsWith("/") ? "" : "/")
					+ "protex/ProtexIPIdentifyFolderBillOfMaterialsContainer?isAtTop=true&ProtexIPProjectId="
					+ projectPojo.getProjectKey() + "&ProtexIPIdentifyFileViewLevel=folder&ProtexIPIdentifyFileId=-1";

			sonarQubeApplication.setProtexBomURL(protexBomURL);

			// Pack some information about Protex
			final BDSProtexPojo protexInfo = new BDSProtexPojo(projectPojo);
			protexInfo.setProtexServer(serverURL);
			protexInfo.setProtexBomURL(protexBomURL);
			sonarQubeApplication.setProtexProject(protexInfo);

		} catch (final CommonFrameworkException cfe) {
			log.error("Could not get project information for: " + projectName);
			log.error("Error: " + cfe.getMessage());
			log.error("Configuration Information: " + cfe.getConfigurationInformation());
			sonarQubeApplication.setProtexErrorMsg(cfe.getMessage());
		} catch (final Exception e) {
			log.error("Error: " + e.getMessage());
			sonarQubeApplication.setProtexErrorMsg(e.getMessage());
		}
		log.info("Got project information for: " + projectName);
	}

	/**
	 * As of 2.0.1 - Refactored to use 7.x SDK
	 *
	 * @param applicationPojo
	 * @param sensorContext
	 */
	public void populateProjectFileCounts(final ApplicationPOJO applicationPojo, final SensorContext sensorContext) {
		Long totalFiles = new Long(0);
		Long totalPendingFiles = new Long(0);
		Long noDiscoveryCount = new Long(0);
		Long discoveryCount = new Long(0);
		Long licenseConflictCount = new Long(0);
		Long skippedFiles = new Long(0);

		final CodeTreeHelper treeHelper = protexWrapper.getCodeTreeHelper();
		final CodeTreeApi codeTreeApi = protexWrapper.getInternalApiWrapper().getCodeTreeApi();

		final ProjectPojo protexProjectPojo = applicationPojo.getProtexProject();

		try {
			final Map<NodeCountType, Long> allCounts = treeHelper.getAllCountsForProjects(protexProjectPojo);

			totalFiles = allCounts.get(NodeCountType.FILES);
			totalPendingFiles = allCounts.get(NodeCountType.PENDING_ID_ALL);
			noDiscoveryCount = allCounts.get(NodeCountType.NO_DISCOVERIES);
			discoveryCount = allCounts.get(NodeCountType.DISCOVERIES);
			licenseConflictCount = allCounts.get(NodeCountType.LICENSE_CONFLICTS);
			// The only count not available from the map.
			skippedFiles = codeTreeApi.getSkippedFileCount(applicationPojo.getProjectID());

		} catch (final Exception e) {
			log.error("Error getting Protex project counts: " + e.getMessage());
		}

		applicationPojo.setTotalFileCount(totalFiles);
		applicationPojo.setTotalPendingFileCount(totalPendingFiles);
		applicationPojo.setTotalSkippedFileCount(skippedFiles);
		applicationPojo.setTotalNoDiscoveryCount(noDiscoveryCount);
		applicationPojo.setTotalLicenseConflictCount(licenseConflictCount);

		// This is the only value we calculate
		// But we must do it here and then store it as a metric to get Sonar
		// trending.
		discoveryCount = totalFiles - noDiscoveryCount;
		applicationPojo.setTotalDiscoveryCount(discoveryCount);

	}

	public LicensePOJO populateLicenseAttributes(final LicensePOJO licensePojo) {
		final String lic_id = licensePojo.getLicenseID();
		try {
			final GlobalLicense lic = licenseApi.getLicenseById(lic_id);
			final LicenseAttributes attributes = lic.getAttributes();
			final LicenseExtensionLevel licLevel = attributes.getIntegrationLevelForLicenseApplication();

			final String licenseReachName = licLevel.toString();
			if (licenseReachName == null) {
				log.warn("Could not get license level reach name for license ID: " + lic_id);
			} else {
				licensePojo.setLicenseReachString(licenseReachName);
			}

			final Integer ordinalNumber = licLevel.ordinal();
			if (ordinalNumber == null) {
				log.warn("Could not get ordinal value for license ID: " + lic_id);
			} else {
				licensePojo.setLicenseReachNumber(ordinalNumber);
			}

		} catch (final Exception e) {
			log.error("Error: " + e.getMessage());
		}

		return licensePojo;
	}

}
