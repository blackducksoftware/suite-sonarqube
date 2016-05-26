package com.blackducksoftware.soleng.bdsplugin.config;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public class BDSPluginProtexConfigManager extends ConfigurationManager {

    private String protexPojectName;;

    public BDSPluginProtexConfigManager(CommonUser user) {
        super(user, APPLICATION.PROTEX);
    }

    public String getProtexPojectName() {
        return protexPojectName;
    }

    public void setProtexPojectName(String protexPojectName) {
        this.protexPojectName = protexPojectName;
    }

}
