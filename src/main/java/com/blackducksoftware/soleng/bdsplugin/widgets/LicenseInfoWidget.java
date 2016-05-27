package com.blackducksoftware.soleng.bdsplugin.widgets;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

import com.blackducksoftware.soleng.bdsplugin.BDSPlugin;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;

@UserRole(UserRole.USER)
@WidgetCategory("BlackDuck")
@Description("A breakdown of the current licenses.")
public class LicenseInfoWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	private static Logger log = LoggerFactory.getLogger(LicenseInfoWidget.class.getName());
	private String ruby_template_file = "lic_info.html.erb";

	@Override
	public String getId() {
		return "licinfo";
	}

	@Override
	public String getTitle() {
		return "License Info";
	}

	@Override
	protected String getTemplatePath() {

		if (BDSPlugin.devMode) {
			String workspace = BDSPluginConstants.DEV_LOCAL_ECLIPSE_PROJECT;
			log.warn("RUNNING IN DEV MODE!!!");
			String absolutePath = workspace + File.separator + ruby_template_file;
			log.info("Using absolute path: " + absolutePath);
			return absolutePath;
		}

		return "/com/blackducksoftware/soleng/" + ruby_template_file;

	}

}
