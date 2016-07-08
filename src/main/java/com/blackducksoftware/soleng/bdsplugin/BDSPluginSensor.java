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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Project;

import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.google.gson.Gson;

public class BDSPluginSensor implements Sensor {

	private static Logger log = LoggerFactory.getLogger(BDSPluginSensor.class.getName());

	private ApplicationPOJO pojo = new ApplicationPOJO();
	private Settings settings = null;

	public static void main(final String[] args) {
		// Not called
	}

	/**
	 * Optional constructor if you want to utilize the properties set forth by
	 * the Plugin class Settings are
	 *
	 * @param configuration
	 */
	public BDSPluginSensor(final Settings settings) {
		this.settings = settings;
	}

	@Override
	public boolean shouldExecuteOnProject(final Project arg0) {
		return codeCenterIsConfgured(settings) || protexIsConfgured(settings);
	}

	@Override
	public void analyse(final Project sonarProject, final SensorContext sensorContext) {
		if (codeCenterIsConfgured(settings)) {
			analyseCodeCenter(sonarProject, sensorContext);
		} else {
			log.info("Code Center not configured: skip analysis");
			pojo.setCcErrorMsg("Code Center not configured: no data");
		}
		if (protexIsConfgured(settings)) {
			analyseProtex(sonarProject, sensorContext);
		} else {
			log.info("Protex not configured: skip analysis");
			pojo.setProtexErrorMsg("Protex not configured: no data");
		}
		log.info("Finished: " + pojo.toString());
		saveMetrics(pojo, sensorContext);
	}

	private boolean codeCenterIsConfgured(final Settings settings) {
		return !(StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_CC_URL))
				|| StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_CC_USERNAME))
				|| StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_CC_PASSSWORD)));
	}

	private boolean protexIsConfgured(final Settings settings) {
		return !(StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_PROTEX_URL))
				|| StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_PROTEX_USERNAME))
				|| StringUtils.isEmpty(settings.getString(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD)));
	}

	private void analyseCodeCenter(final Project sonarProject, final SensorContext sensorContext) {
		CodeCenterConnector ccConecctor = null;

		// Code Center should be initialized first so that it can get associated
		// project info.
		// Catch authentication exceptions and store the errors.
		try {
			ccConecctor = new CodeCenterConnector(settings, sonarProject);
		} catch (Exception e) {
			log.error("Unable to authenticate Code Center, cause: " + e.getMessage());
			pojo.setCcErrorMsg(e.getMessage());
		}

		// Get the basic stuff
		if (ccConecctor != null) {
			pojo = ccConecctor.populateApplicationPojo(pojo);
		}
	}

	private void analyseProtex(final Project sonarProject, final SensorContext sensorContext) {
		ProtexConnector protexConnector = null;

		try {
			protexConnector = new ProtexConnector(settings, sonarProject);
		} catch (Exception e) {
			log.error("Unable to authenticate Protex, cause: " + e.getMessage());
			pojo.setProtexErrorMsg(e.getMessage());
		}

		if (protexConnector != null) {
			pojo = protexConnector.populateApplicationWithProtexData(pojo, sensorContext);

			// Determine license breakdown
			pojo = protexConnector.populateLicenseData(pojo);
		}

	}

	/**
	 * Save all the application pojo metrics inside the Sonar DB
	 *
	 * @param pojo
	 * @param sensorContext
	 */
	private void saveMetrics(final ApplicationPOJO pojo, final SensorContext sensorContext) {
		try {
			// Errors if any
			saveMetricString(sensorContext, pojo.getProtexErrorMsg(), BDSPluginMetrics.PROTEX_ERROR_MESSAGE);
			saveMetricString(sensorContext, pojo.getCcErrorMsg(), BDSPluginMetrics.CC_ERROR_MESSAGE);

			// Protex data
			saveMetricLong(sensorContext, pojo.getTotalFileCount(), BDSPluginMetrics.PROTEX_TOTAL_FILES);
			saveMetricLong(sensorContext, pojo.getTotalPendingFileCount(), BDSPluginMetrics.PROTEX_PENDING_FILES);
			saveMetricLong(sensorContext, pojo.getTotalNoDiscoveryCount(), BDSPluginMetrics.PROTEX_NO_DISCOVERY_FILES);
			saveMetricLong(sensorContext, pojo.getTotalDiscoveryCount(), BDSPluginMetrics.PROTEX_DISCOVERY_FILES);
			saveMetricLong(sensorContext, pojo.getTotalLicenseConflictCount(),
					BDSPluginMetrics.PROTEX_LICENSE_CONFLICT_FILES);
			saveMetricString(sensorContext, pojo.getProjectName(), BDSPluginMetrics.PROTEX_ASSOCIATED_PROJECT);
			saveMetricString(sensorContext, pojo.getDateLastAnalyzed(), BDSPluginMetrics.PROTEX_ANALYZED_DATE);
			saveMetricJson(sensorContext, pojo.getProtexProject(), BDSPluginMetrics.PROTEX_INFO_JSON);

			/*
			 * REQUESTS
			 */
			saveMetricString(sensorContext, pojo.getCodeCenterBomPage(), BDSPluginMetrics.CC_APP_BOM_URL);
			// Component Request Measures
			saveMetricInt(sensorContext, pojo.getApprovedComponentCount(), BDSPluginMetrics.CC_REQUESTS_APPROVED);
			saveMetricInt(sensorContext, pojo.getPendingComponentCount(), BDSPluginMetrics.CC_REQUESTS_PENDING);
			saveMetricInt(sensorContext, pojo.getRejectedComponentCount(), BDSPluginMetrics.CC_REQUESTS_REJECTED);
			saveMetricInt(sensorContext, pojo.getUnknownComponentCount(), BDSPluginMetrics.CC_REQUESTS_NS);
			// Component Request JSON
			saveMetricJson(sensorContext, pojo.getComponentsApproved(), BDSPluginMetrics.COMP_LIST_APPROVED_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsPending(), BDSPluginMetrics.COMP_LIST_PENDING_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsRejected(), BDSPluginMetrics.COMP_LIST_REJECTED_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsUnknown(), BDSPluginMetrics.COMP_LIST_NS_JSON);
			// Custom Attributes
			saveMetricJson(sensorContext, pojo.getAttributes(), BDSPluginMetrics.CC_CUSTOM_ATTIBUTES_JSON);

			/*
			 * VULNERABILITIES
			 */

			// CC Vulnerabilities
			saveMetricInt(sensorContext, pojo.getVulnHighCount(), BDSPluginMetrics.CC_VULN_HIGH_COUNT);
			saveMetricInt(sensorContext, pojo.getVulnMedCount(), BDSPluginMetrics.CC_VULN_MED_COUNT);
			saveMetricInt(sensorContext, pojo.getVulnLowCount(), BDSPluginMetrics.CC_VULN_LOW_COUNT);

			// Vulnerabilities JSON
			saveMetricJson(sensorContext, pojo.getVulnHighList(), BDSPluginMetrics.VULN_LIST_HIGH_JSON);
			saveMetricJson(sensorContext, pojo.getVulnMedList(), BDSPluginMetrics.VULN_LIST_MED_JSON);
			saveMetricJson(sensorContext, pojo.getVulnLowList(), BDSPluginMetrics.VULN_LIST_LOW_JSON);

			// Licenses for the chart
			saveMetricJson(sensorContext, pojo.getSortedLicenseMap(), BDSPluginMetrics.LICENSE_BREAKDOWN_JSON);

		} catch (Exception e) {
			log.warn("Unable to save all metrics!", e);
		}
	}

	private void saveMetricDate(final SensorContext sensorContext, final Date value, final Metric metric) {
		final Measure measure = new Measure(metric);
		measure.setDate(value);

		if (sensorContext != null) {
			sensorContext.saveMeasure(measure);
		}

		log.info("Saved new measure: " + measure.toString());
	}

	private void saveMetricString(final SensorContext sensorContext, final String value, final Metric metric) {
		try {
			final Measure measure = new Measure(metric);
			measure.setData(value);

			measure.setPersistenceMode(PersistenceMode.DATABASE);

			if (sensorContext != null) {
				sensorContext.saveMeasure(measure);
			}

			log.info("Saved new measure: " + measure.toString());

		} catch (Exception e) {
			log.warn("Unable to save metric: " + metric.getName(), e);
		}

	}

	/**
	 * Converts the objects into a JSON string and saved that as the metric
	 *
	 * @param sensorContext
	 * @param sortedLicenseMap
	 * @param licenseBreakdownJson
	 */
	private void saveMetricJson(final SensorContext sensorContext, final Object collection,
			final Metric licenseBreakdownJson) {
		Gson gson = new Gson();

		String value = gson.toJson(collection);

		final Measure measure = new Measure(licenseBreakdownJson);
		measure.setData(value);
		// TODO: Investigate whether this is the best approach.
		// measure.setPersistenceMode(PersistenceMode.DATABASE);

		if (sensorContext != null) {
			sensorContext.saveMeasure(measure);
		}

		log.info("Saved new measure: " + measure.toString());

	}

	private void saveMetricInt(final SensorContext sensorContext, final Integer value, final Metric metric) {
		final Measure measure = new Measure(metric);
		measure.setIntValue(value);

		if (sensorContext != null) {
			sensorContext.saveMeasure(measure);
		}

		log.info("Saved new measure: " + measure.toString());
	}

	/**
	 * Sonar does not allow for longs, convert it.
	 *
	 * @param sensorContext
	 * @param value
	 * @param metric
	 */
	private void saveMetricLong(final SensorContext sensorContext, final Long value, final Metric metric) {
		final Measure measure = new Measure(metric);
		measure.setIntValue(value.intValue());

		if (sensorContext != null) {
			sensorContext.saveMeasure(measure);
		}

		log.info("Saved new measure: " + measure.toString());
	}

}
