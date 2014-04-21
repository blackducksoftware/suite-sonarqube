package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("Component Approval Plugin, list of component and their approval status.")
public class CompApproveWidget extends AbstractRubyTemplate implements RubyRailsWidget {

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
		//return "C:\\eclipse_workspaces\\BDSPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\comp_approve.html.erb";
		 return "/com/blackducksoftware/soleng/comp_approve.html.erb";
		
	}

}
