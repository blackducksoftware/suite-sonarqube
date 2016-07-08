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

import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;

/**
 * Little bean to hold Protex data that will be displayed in the Component
 * Analaysis 'info' tooltip
 *
 * Extends those methods which are not accessible via the common framework.
 *
 */

public class BDSProtexPojo extends ProtexProjectPojo {

	private String protexServer = null;

	private String protexBomURL = null;

	private ProtexProjectPojo protexProjectPojo = null;

	public BDSProtexPojo(final ProtexProjectPojo projectPojo) {
		setProtexProjectPojo(projectPojo);
	}

	public String getProtexServer() {
		return protexServer;
	}

	public void setProtexServer(final String protexServer) {
		this.protexServer = protexServer;
	}

	public String getProtexBomURL() {
		return protexBomURL;
	}

	public void setProtexBomURL(final String protexBomURL) {
		this.protexBomURL = protexBomURL;
	}

	public ProtexProjectPojo getProtexProjectPojo() {
		return protexProjectPojo;
	}

	private void setProtexProjectPojo(final ProtexProjectPojo protexProjectPojo) {
		this.protexProjectPojo = protexProjectPojo;
		super.setProjectKey(protexProjectPojo.getProjectKey());
		super.setProjectName(protexProjectPojo.getProjectName());
	}

}
