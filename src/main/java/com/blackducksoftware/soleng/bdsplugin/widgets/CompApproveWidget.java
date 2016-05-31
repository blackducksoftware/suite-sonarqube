package com.blackducksoftware.soleng.bdsplugin.widgets;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("Component Approval Plugin, list of component and their approval status.")
public class CompApproveWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static Logger log = LoggerFactory.getLogger(CompApproveWidget.class.getName());
	private String ruby_template_file = "comp_approve.html.erb";

	@Override
	public String getId() {
		return "compapprove";
	}

	@Override
	public String getTitle() {
		return "Component Approval";
	}

	@Override
	protected String getTemplatePath() {

		if (BDSPluginConstants.DEV_MODE) {
			String workspace = BDSPluginConstants.DEV_LOCAL_ECLIPSE_PROJECT;
			log.warn("RUNNING IN DEV MODE!!!");
			String absolutePath = workspace + File.separator + ruby_template_file;
			log.info("Using absolute path: " + absolutePath);
			return absolutePath;
		}

		return "/com/blackducksoftware/soleng/" + ruby_template_file;
	}

}
