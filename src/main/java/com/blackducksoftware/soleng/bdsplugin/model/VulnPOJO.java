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
package com.blackducksoftware.soleng.bdsplugin.model;

import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO.VULNERABILITY_SEVERITY;

/**
 * Basic bean that contains vulnerability information
 * 
 */
public class VulnPOJO {

	private String name;
	private String publishDate;
	private String comments;
	private String description;
	private String componentName;
	private String componentVersion;

	private VULNERABILITY_SEVERITY severity;

	public VulnPOJO(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(final String publishDate) {
		this.publishDate = publishDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public VULNERABILITY_SEVERITY getSeverity() {
		return severity;
	}

	public void setSeverity(final VULNERABILITY_SEVERITY severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(final String componentName) {
		this.componentName = componentName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + this.name);
		sb.append("\n");
		sb.append("Belongs to component: " + this.componentName);
		sb.append("\n");
		sb.append("Severity: " + this.severity);
		sb.append("\n");

		return sb.toString();
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public void setComponentVersion(final String componentVersion) {
		this.componentVersion = componentVersion;
	}

}
