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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import com.blackducksoftware.soleng.bdsplugin.dao.CodeCenterDAO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;

public class CodeCenterConnector {

	private static Logger log = LoggerFactory.getLogger(CodeCenterConnector.class.getName());

	private CodeCenterDAO ccDAO = null;

	private Settings settings = null;

	public CodeCenterConnector(final Settings settings, final Project sonarProject) throws Exception {
		this.settings = settings;
		ccDAO = new CodeCenterDAO(settings, sonarProject);
	}

	public ApplicationPOJO populateApplicationPojo(ApplicationPOJO pojo) {
		try {
			pojo = ccDAO.populateApplicationInfo(pojo);
			// FIXME CC-13128
			/*
			 * Dangerous workaround for CC-13128 Don't change the order of the
			 * following statements
			 */
			pojo = ccDAO.populateComponentBreakdown(pojo);
			pojo = ccDAO.populateURLs(pojo, settings);
			// FIXME CC-13128
			// Optional depending on settings.
			pojo = ccDAO.collectCustomAttributes(pojo);

		} catch (Exception e) {
			log.error("Unable to populate Code Center data: " + e.getMessage());
			pojo.setCcErrorMsg(e.getMessage());
		}

		log.info("App information: " + pojo.toString());

		return pojo;
	}

}
