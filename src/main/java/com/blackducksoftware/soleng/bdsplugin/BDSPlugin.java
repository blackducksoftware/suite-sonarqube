package com.blackducksoftware.soleng.bdsplugin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import com.blackducksoftware.soleng.bdsplugin.widgets.CompAnalysisWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.CompApproveWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.CompVulnWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.LicenseInfoWidget;
import com.google.common.collect.ImmutableList;

/**
 * Plugin class that defines all the properties.
 * 
 * @author Ari
 * 
 */

public class BDSPlugin extends SonarPlugin {

	protected final static String BD_CATEGORY = "BlackDuck";
	protected final static String CC_SUB_CATEGORY = "Code Center";
	protected final static String PROTEX_SUB_CATEGORY = "Protex";
	protected final static String GENERAL_CATEGORY = " General";
	private static final Logger log = LoggerFactory.getLogger(ProtexConnector.class);

	private String sonarProjectName = "";
	private String sonarProjectKey = "";

	private List<String> PROXY_PROTOCOLS = new ArrayList<String>();

	public BDSPlugin() {
	}

	@Override
	public List<?> getExtensions() {

		log.info("Building settings for project password: " + sonarProjectName);
		log.info("Building settings for project key: " + sonarProjectKey);

		PROXY_PROTOCOLS.add(BDSPluginConstants.PROXY_PROTOCOL_HTTP);
		PROXY_PROTOCOLS.add(BDSPluginConstants.PROXY_PROTOCOL_SSL);

		return ImmutableList
				.of(PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_URL).name("Code Center Server URL")
						.description("The full URL of the code center server, example: http://your server/")
						.category(BD_CATEGORY).subCategory(CC_SUB_CATEGORY).type(PropertyType.STRING).index(0).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_USERNAME)
								.name("Code Center user name").description("Your code center login name")
								.category(BD_CATEGORY).subCategory(CC_SUB_CATEGORY).type(PropertyType.STRING).index(1)
								.build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_PASSSWORD)
								.name("Code Center Password").description("Your CC password").category(BD_CATEGORY)
								.subCategory(CC_SUB_CATEGORY).type(PropertyType.PASSWORD).index(2).build(),

						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_PROJECT).name("Code Center App Name")
								.description("Name of your Code Center Application").category(BD_CATEGORY)
								.onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).subCategory(CC_SUB_CATEGORY)
								.type(PropertyType.STRING).index(0).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_VERSION)
								.name("Version of your Code Center Application")
								.description("Version of your Code Center Application").category(BD_CATEGORY)
								.onlyOnQualifiers(Qualifiers.PROJECT).subCategory(CC_SUB_CATEGORY)
								.type(PropertyType.STRING).index(1).build(),

						// Protex Properties
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_URL).name("Protex Server URL")
								.description("The full URL of the protex server.").category(BD_CATEGORY)
								.onQualifiers(Qualifiers.PROJECT) // General and
																	// project
								.subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.STRING).index(0).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_USERNAME)
								.name("Protex User Name").description("Your Protex account user name")
								.category(BD_CATEGORY).onQualifiers(Qualifiers.PROJECT) // General
																						// and
																						// project
								.subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.STRING).index(1).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD).name("Protex Password")
								.description("Your Protex password").category(BD_CATEGORY)
								.onQualifiers(Qualifiers.PROJECT) // General and
																	// project
								.subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.PASSWORD).index(2).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_REFRESH_BOM_DATE)
								.defaultValue("January 1, 2012").name("BOM Refresh").category(BD_CATEGORY)
								.subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.STRING).index(3).hidden().build(),
						PropertyDefinition
								.builder(BDSPluginConstants.PROPERTY_PROTEX_PROJECT)
								.defaultValue(sonarProjectName)
								.name("Name of Protex Project")
								.description(
										"This should be automatically configured within Code Center, but you can override here.")
								.onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).category(BD_CATEGORY)
								.subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.STRING).index(4).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_LICENSE_COLORS)
								.name("Protex License Category Colors")
								.description("Assign colors per license type, example: permissive=red,unknown=orange")
								.category(BD_CATEGORY).subCategory(PROTEX_SUB_CATEGORY).type(PropertyType.STRING)
								.index(5).hidden().build(),

						// Proxy
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROXY_SERVER).name("Proxy Server")
								.description("Server for your proxy.").category(BD_CATEGORY)
								.subCategory(GENERAL_CATEGORY).type(PropertyType.STRING).index(0).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROXY_PORT).name("Proxy Port")
								.description("Port for your proxy.").category(BD_CATEGORY)
								.subCategory(GENERAL_CATEGORY).type(PropertyType.STRING).index(1).build(),
						PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROXY_PROTOCOL)
								.name("Protocol for your proxy").defaultValue(BDSPluginConstants.PROXY_PROTOCOL_HTTP)
								.description("HTTP or SSL").category(BD_CATEGORY).subCategory(GENERAL_CATEGORY)
								.type(PropertyType.SINGLE_SELECT_LIST).options(PROXY_PROTOCOLS).index(2).build(),

						BDSPluginMetrics.class, BDSPluginSensor.class, CompAnalysisWidget.class,
						LicenseInfoWidget.class, CompApproveWidget.class, CompVulnWidget.class);
	}
}
