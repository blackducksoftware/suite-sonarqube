package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

import com.blackducksoftware.soleng.bdsplugin.BDSPlugin;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("List of vulnerabilities belonging to your components.")
public class CompVulnWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static Logger log = LoggerFactory.getLogger(CompVulnWidget.class.getName());
	
	
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
		if(BDSPlugin.devMode)
		{
			log.warn("RUNNING IN DEV MODE!!!");
			return "C:\\eclipse_workspaces\\BDSSonarPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\comp_vuln.html.erb";
		}
		
		return "/com/blackducksoftware/soleng/comp_vuln.html.erb";
		
	}

}
