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
@Description("Component Approval Plugin, list of component and their approval status.")
public class CompApproveWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static Logger log = LoggerFactory.getLogger(CompApproveWidget.class.getName());
	
	@Override
	public String getId() {
		return "compapprove";
	}

	@Override
	public String getTitle() {
		return "Component Approval";
	}

	@Override
	protected String getTemplatePath() 
	{
		if(BDSPlugin.devMode)
		{
			log.warn("RUNNING IN DEV MODE!!!");
			return "C:\\eclipse_workspaces\\BDSSonarPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\comp_approve.html.erb";
		}
		
		return "/com/blackducksoftware/soleng/comp_approve.html.erb";
		
	}

}
