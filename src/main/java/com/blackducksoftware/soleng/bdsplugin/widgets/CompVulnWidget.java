package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("List of vulnerabilities belonging to your components.")
public class CompVulnWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	@Override
	public String getId() {
		return "compvuln";
	}

	@Override
	public String getTitle() {
		return "Vulnerabilities";
	}

	@Override
	protected String getTemplatePath() 
	{
		//return "C:\\eclipse_workspaces\\BDSPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\comp_vuln.html.erb";

		 return "/com/blackducksoftware/soleng/comp_vuln.html.erb";
		
	}

}
