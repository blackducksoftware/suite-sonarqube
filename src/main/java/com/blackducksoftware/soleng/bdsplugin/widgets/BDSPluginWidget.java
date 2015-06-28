package com.blackducksoftware.soleng.bdsplugin.widgets;

/**
 * This is not used, but can be hooked back up for easy testing.
 */
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
@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("Black Duck Plugin for Sonar")


public final class BDSPluginWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	
  public String getId() {
    return "CCSonarPlugin";
  }

  public String getTitle() {
    return "Black Duck General Plugin";
  }

  @Override
  protected String getTemplatePath() 
  {
	//TODO:  Revert back to relative pathing after you are done testing.
//	String file = "C:\\eclipse_workspaces\\BDSPlugin\\src\\main\\resources\\com\\blackducksoftware\\soleng\\ccsonar_widget.html.erb";

    String file = "/com/blackducksoftware/soleng/ccsonar_widget.html.erb";
	
	return file;
  }

}
