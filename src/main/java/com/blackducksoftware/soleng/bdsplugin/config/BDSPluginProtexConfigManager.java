package com.blackducksoftware.soleng.bdsplugin.config;

import soleng.framework.core.config.ConfigConstants;
import soleng.framework.core.config.ConfigurationManager;
import soleng.framework.core.config.ProtexConfigurationManager;
import soleng.framework.core.config.user.CommonUser;

public class BDSPluginProtexConfigManager extends ConfigurationManager {

	private String protexPojectName;;
	
	public BDSPluginProtexConfigManager(CommonUser user) {
		super(user, ConfigConstants.APPLICATION.PROTEX);
	}

	public String getProtexPojectName() {
		return protexPojectName;
	}

	public void setProtexPojectName(String protexPojectName) {
		this.protexPojectName = protexPojectName;
	}

}
