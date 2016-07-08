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
/**
 *
 */
package com.blackducksoftware.soleng.bdsplugin.model;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;

/**
 * Class that holds that basic component information
 */
public class CompPOJO {

	private String componentName = null;
	private String version = null;
	private String id = null;
	private String requestType = null;
	private String url = null;
	private ApplicationIdToken appIdToken = null; // This will only be non-null
													// if the component is a
													// rolled up App.
	private KbComponentIdToken kbCompId = null; // Temporary workaround until
												// the appid gets fixed.

	// Counts for easy access
	private Integer vulnHigh = new Integer(0);
	private Integer vulnMed = new Integer(0);
	private Integer vulnLow = new Integer(0);

	public CompPOJO(final String name, final String version) {
		this.componentName = name;
		this.version = version;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(final String componentName) {
		this.componentName = componentName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(final String requestType) {
		this.requestType = requestType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("\n");
		buff.append("Name: " + getComponentName());
		buff.append("\n");
		buff.append("Version: " + getVersion());
		buff.append("\n");
		buff.append("ID: " + getId());
		buff.append("\n");
		buff.append("Approval Status: " + getRequestType());
		buff.append("\n");
		buff.append("URL: " + url);
		buff.append("\n");
		buff.append("Vulnerability High: " + getVulnHigh());
		buff.append("\n");
		buff.append("Vulnerability Med: " + getVulnMed());
		buff.append("\n");
		buff.append("Vulnerability Low: " + getVulnLow());
		buff.append("\n");
		return buff.toString();

	}

	public Integer getVulnHigh() {
		return vulnHigh;
	}

	public Integer getVulnMed() {
		return vulnMed;
	}

	public Integer getVulnLow() {
		return vulnLow;
	}

	public void addVulnLow() {
		this.vulnLow = vulnLow + 1;
	}

	public void addVulnHigh() {
		this.vulnHigh = vulnHigh + 1;
	}

	public void addVulnMed() {
		this.vulnMed = vulnMed + 1;
	}

	public ApplicationIdToken getAppIdToken() {
		return appIdToken;
	}

	public void setAppIdToken(final ApplicationIdToken appIdToken) {
		this.appIdToken = appIdToken;
	}

	public KbComponentIdToken getKbCompId() {
		return kbCompId;
	}

	public void setKbCompId(final KbComponentIdToken kbCompId) {
		this.kbCompId = kbCompId;
	}

}
