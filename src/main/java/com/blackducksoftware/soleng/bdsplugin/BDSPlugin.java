package com.blackducksoftware.soleng.bdsplugin;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import com.blackducksoftware.soleng.bdsplugin.model.CCPieChart3D;
import com.blackducksoftware.soleng.bdsplugin.widgets.CompAnalysisWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.CompApproveWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.CompVulnWidget;
import com.blackducksoftware.soleng.bdsplugin.widgets.LicenseInfoWidget;
import com.google.common.collect.ImmutableList;

/**
 *  Plugin class that defines all the properties.
 * @author Ari 
 *
 */

public class BDSPlugin extends SonarPlugin 
{

	protected final static String BD_CATEGORY = "BlackDuck";
	protected final static String CC_SUB_CATEGORY = "Code Center";
	protected final static String PROTEX_SUB_CATEGORY = "Protex";
	
	// Used for the UI to return static ruby templates 
	public static final Boolean devMode = new Boolean(true);
	
  /**
   * Add the jfreechart extension modules to display "prettier" graphics	
   */
  public List<?> getExtensions() 
  {

    return ImmutableList.of(
    	      PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_URL)
    	        .defaultValue("http://satemplatecc1/")
    	        .name("Code Center Server URL")
    	        .description("The full URL of the code center server, example: http://your server/")
    	        .category(BD_CATEGORY)
    	        .subCategory(CC_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(0)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_USERNAME)
    	        .defaultValue("akamen")
    	        .name("Code Center user name")
    	        .description("Your code center login name")
    	        .category(BD_CATEGORY)
    	        .subCategory(CC_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(1)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_PASSSWORD)
    	        .defaultValue("")
    	        .name("Code Center Password")
    	        .description("Your CC password")
    	        .category(BD_CATEGORY)
    	        .subCategory(CC_SUB_CATEGORY)
    	        .type(PropertyType.PASSWORD)
    	        .index(2)
    	        .build(),
    	        
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_PROJECT)
    	        .defaultValue("Ari_App")
    	        .name("Code Center App Name")
    	        .description("Name of your Code Center Application")
    	        .category(BD_CATEGORY)
    	        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
    	        .subCategory(CC_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(0)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_CC_VERSION)
    	        .defaultValue("1.0")
    	        .name("Version of your Code Center Application")
    	        .description("Version of your Code Center Application")
    	        .category(BD_CATEGORY)
    	        .onlyOnQualifiers(Qualifiers.PROJECT)
    	        .subCategory(CC_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(1)
    	        .build(),
    	        
    	        // Protex Properties
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_URL)
    	        .defaultValue("https://kiowa.blackducksoftware.com/")
    	        .name("Protex Server URL")
    	        .description("The full URL of the protex server")
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(0)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_USERNAME)
    	        .defaultValue("akamen@blackducksoftware.com")
    	        .name("Protex User Name")
    	        .description("Your Protex account user name")
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(1)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD)
    	        .defaultValue("")
    	        .name("Protex Password")
    	        .description("Your Protex password")
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.PASSWORD)
    	        .index(2)
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_REFRESH_BOM_DATE)
    	        .defaultValue("January 1, 2012")
    	        .name("BOM Refresh")
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(3)
    	        .hidden()
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_LICENSE_COLORS)
    	        .defaultValue("")
    	        .name("Protex License Category Colors")
    	        .description("Assign colors per license type, example: permissive=red,unknown=orange")
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(3)
    	        .hidden()
    	        .build(),
    	        PropertyDefinition.builder(BDSPluginConstants.PROPERTY_PROTEX_PROJECT)
    	        .name("Name of Protex Project")
    	        .description("This should be automatically configured within Code Center, but you can override here.")
    	        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
    	        .category(BD_CATEGORY)
    	        .subCategory(PROTEX_SUB_CATEGORY)
    	        .type(PropertyType.STRING)
    	        .index(0)
    	        .build(),
    	        
    		BDSPluginMetrics.class, 
    		BDSPluginSensor.class, 
    		CompAnalysisWidget.class,
    		LicenseInfoWidget.class,
    		CompApproveWidget.class,
    		CompVulnWidget.class);
  }
}