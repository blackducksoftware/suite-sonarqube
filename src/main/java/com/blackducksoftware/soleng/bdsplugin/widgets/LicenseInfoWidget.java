package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;

import com.blackducksoftware.soleng.bdsplugin.BDSPlugin;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("A breakdown of the current licenses.")

public class LicenseInfoWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static Logger log = LoggerFactory.getLogger(LicenseInfoWidget.class.getName());
	
	@Override
	public String getId() {
		return "licinfo";
	}

	@Override
	public String getTitle() {
		return "License Info";
	}

	@Override
	protected String getTemplatePath() 
	{
		if(BDSPlugin.devMode)
		{
			log.warn("RUNNING IN DEV MODE!!!");
			return "C:\\eclipse_workspaces\\BDSSonarPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\lic_info.html.erb";
		}
		
		return "/com/blackducksoftware/soleng/lic_info.html.erb";
		
	}

}
