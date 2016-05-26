package com.blackducksoftware.soleng.bdsplugin.dao;

import org.sonar.api.config.Settings;

import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.ProxyBean;

public class CommonDAO implements SDKDAO {

    /**
     * 
     * 
     * Collects all general settings from SonarQube's internal setting maps
     * and assigns them to the appropriate configuration members.
     * 
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

        ProxyBean cfProxyBean = cm.getProxyBean();

        if (PROXY_PROTOCOL != null && PROXY_PROTOCOL.equals(BDSPluginConstants.PROXY_PROTOCOL_HTTP))
        {
            cfProxyBean.setProxyServer(PROXY_SERVER);
            cfProxyBean.setProxyPort(PROXY_PORT);
        }
        else
        {
            cfProxyBean.setProxyServerHttps(PROXY_SERVER);
            cfProxyBean.setProxyPortHttps(PROXY_PORT);
        }

        // TODO: Expose setBean in common framework base
        // cm.setProxyBean(cfProxyBean);

        return cm;
    }

    @Override
    public void authenticate() throws Exception {
        // Should be overriden
    }

}
