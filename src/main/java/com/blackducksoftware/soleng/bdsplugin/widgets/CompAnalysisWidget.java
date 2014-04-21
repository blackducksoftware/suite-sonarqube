package com.blackducksoftware.soleng.bdsplugin.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("Summary of Protex data.")
public class CompAnalysisWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	@Override
	public String getId() 
	{
		return "compAn";
	}

	@Override
	public String getTitle() {
		return "Component Analysis";
	}

	@Override
	protected String getTemplatePath() 
	{
		//return "C:\\eclipse_workspaces\\BDSPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\comp_analysis.html.erb";
	   
		return "/com/blackducksoftware/soleng/comp_analysis.html.erb";
	}

}
