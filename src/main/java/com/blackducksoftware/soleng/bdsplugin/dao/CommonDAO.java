package com.blackducksoftware.soleng.bdsplugin.dao;

import org.sonar.api.config.Settings;

import soleng.framework.core.config.ConfigurationManager;

import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;

public class CommonDAO implements SDKDAO {

	/**
	 * 
	 * 
	 * Collects all general settings from SonarQube's internal setting maps
	 * and assigns them to the appropriate configuration members.
	 * @param cm
	 * @param settings
	 * @return
	 * @throws Exception
	 */
	public ConfigurationManager collectGeneralSettings(
			ConfigurationManager cm, Settings settings) throws Exception 
	{
		
		String PROXY_SERVER = settings.getString(BDSPluginConstants.PROPERTY_PROXY_SERVER);
		String PROXY_PORT = settings.getString(BDSPluginConstants.PROPERTY_PROXY_PORT);
		String PROXY_PROTOCOL = settings.getString(BDSPluginConstants.PROPERTY_PROXY_PROTOCOL);
		
		if(PROXY_PROTOCOL != null && PROXY_PROTOCOL.equals(BDSPluginConstants.PROXY_PROTOCOL_HTTP))
		{
			cm.setProxyServer(PROXY_SERVER);
			cm.setProxyPort(PROXY_PORT);
		}
		else
		{
			cm.setProxyServerHttps(PROXY_SERVER);
			cm.setProxyPortHttps(PROXY_PORT);
		}
		
		return cm;
	}
	
	@Override
	public void authenticate() throws Exception {
		// Should be overriden 
	}

}
