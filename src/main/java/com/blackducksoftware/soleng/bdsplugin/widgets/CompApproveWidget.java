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
package com.blackducksoftware.soleng.bdsplugin.widgets;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("Component Approval Plugin, list of component and their approval status.")
public class CompApproveWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private final Logger log = LoggerFactory.getLogger(CompApproveWidget.class.getName());
	private String ruby_template_file = "comp_approve.html.erb";

	@Override
	public String getId() {
		return "compapprove";
	}

	@Override
	public String getTitle() {
		return "Component Approval";
	}

	@Override
	protected String getTemplatePath() {

		if (BDSPluginConstants.DEV_MODE) {
			String workspace = BDSPluginConstants.DEV_LOCAL_ECLIPSE_PROJECT;
			log.warn("RUNNING IN DEV MODE!!!");
			String absolutePath = workspace + File.separator + ruby_template_file;
			log.info("Using absolute path: " + absolutePath);
			return absolutePath;
		}

		return "/com/blackducksoftware/soleng/" + ruby_template_file;
	}

}
