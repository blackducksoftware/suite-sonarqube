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

import org.sonar.api.config.Settings;

import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.ProxyBean;

public class CommonDAO implements SDKDAO {

	/**
	 * 
	 * 
	 * Collects all general settings from SonarQube's internal setting maps and
	 * assigns them to the appropriate configuration members.
	 * 
	 * @param cm
	 * @param settings
	 * @return
	 * @throws Exception
	 */
	public ConfigurationManager collectGeneralSettings(final ConfigurationManager cm, final Settings settings)
			throws Exception {

		String PROXY_SERVER = settings.getString(BDSPluginConstants.PROPERTY_PROXY_SERVER);
		String PROXY_PORT = settings.getString(BDSPluginConstants.PROPERTY_PROXY_PORT);
		String PROXY_PROTOCOL = settings.getString(BDSPluginConstants.PROPERTY_PROXY_PROTOCOL);

		ProxyBean cfProxyBean = cm.getProxyBean();

		if (PROXY_PROTOCOL != null && PROXY_PROTOCOL.equals(BDSPluginConstants.PROXY_PROTOCOL_HTTP)) {
			cfProxyBean.setProxyServer(PROXY_SERVER);
			cfProxyBean.setProxyPort(PROXY_PORT);
		} else {
			cfProxyBean.setProxyServerHttps(PROXY_SERVER);
			cfProxyBean.setProxyPortHttps(PROXY_PORT);
		}

		cm.setProxyBean(cfProxyBean);

		return cm;
	}

	@Override
	public void authenticate() throws Exception {
		// Should be overriden
	}

}
