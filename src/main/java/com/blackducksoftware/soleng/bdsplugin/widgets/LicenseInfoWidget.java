package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("A breakdown of the current licenses.")

public class LicenseInfoWidget extends AbstractRubyTemplate implements RubyRailsWidget {

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
		//return "C:\\eclipse_workspaces\\BDSPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\lic_info.html.erb";
	
		 return "/com/blackducksoftware/soleng/lic_info.html.erb";
		
	}

}
