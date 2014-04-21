package com.blackducksoftware.soleng.bdsplugin;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public final class BDSPluginMetrics implements Metrics {

	/**
	 * Protex Metrics
	 */
  public final static Metric PROTEX_TOTAL_FILES = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_TOTAL_FILES_METRIC_KEY, "Protex Total Files", Metric.ValueType.INT)
    .setDomain(CoreMetrics.DOMAIN_GENERAL)
    .create();

  public final static Metric PROTEX_PENDING_FILES = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_TOTAL_PENDING_METRIC_KEY, "Protex Pending Files", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric PROTEX_NO_DISCOVERY_FILES = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_TOTAL_NO_DISCOVERY_METRIC_KEY, "Protex No Discovery Files", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric PROTEX_DISCOVERY_FILES = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_TOTAL_DISCOVERY_METRIC_KEY, "Protex Discovery Files", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric PROTEX_LICENSE_CONFLICT_FILES = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_TOTAL_LIC_CONFLICT_METRIC_KEY, "Protex License Conflict Files", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric PROTEX_ANALYZED_DATE = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_ANALYZED_DATE_KEY, "Protex Analyzed Date", Metric.ValueType.STRING)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
 
  public final static Metric PROTEX_ASSOCIATED_PROJECT = 
		  new Metric.Builder(BDSPluginConstants.PROTEX_ASSOCIATED_PROJECT_KEY, "Protex Associated Project", Metric.ValueType.STRING)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  /**
   * Code Center Request Metrics
   */ 
  public final static Metric CC_APP_BOM_URL = 
		  new Metric.Builder(BDSPluginConstants.CC_APP_BOM_URL_KEY, "Bom URL", Metric.ValueType.DATA)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  
  public static final Metric COMP_LIST_APPROVED_JSON = new Metric.Builder(BDSPluginConstants.COMP_LIST_APPROVED_JSON_KEY, "Approved Components", Metric.ValueType.DATA)
  .setDescription("JSON list of approved components")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();

  public static final Metric COMP_LIST_PENDING_JSON = new Metric.Builder(BDSPluginConstants.COMP_LIST_PENDING_JSON_KEY, "Pending Components", Metric.ValueType.DATA)
  .setDescription("JSON list of pending components")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public static final Metric COMP_LIST_NS_JSON = new Metric.Builder(BDSPluginConstants.COMP_LIST_NS_JSON_KEY, "Unknown Components", Metric.ValueType.DATA)
  .setDescription("JSON list of unknown components")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
 
  
  public static final Metric COMP_LIST_REJECTED_JSON = new Metric.Builder(BDSPluginConstants.COMP_LIST_REJECTED_JSON_KEY, "Rejected Components", Metric.ValueType.DATA)
  .setDescription("JSON list of rejected components")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_REQUESTS_APPROVED = 
		  new Metric.Builder(BDSPluginConstants.REQUESTS_APPROVED_METRIC_KEY, "Requests Approved", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_REQUESTS_REJECTED = 
		  new Metric.Builder(BDSPluginConstants.REQUESTS_REJECTED_METRIC_KEY, "Requests Rejected", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_REQUESTS_PENDING = 
		  new Metric.Builder(BDSPluginConstants.REQUESTS_PENDING_METRIC_KEY, "Requests Pending", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_REQUESTS_NS = 
		  new Metric.Builder(BDSPluginConstants.REQUESTS_NOT_SUBMITTED_METRIC_KEY, "Requests Not-Submitted", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  /**
   * Code Center Vulnerability data
   */
  
  // Counts
  public final static Metric CC_VULN_HIGH_COUNT = 
		  new Metric.Builder(BDSPluginConstants.VULNERABILITY_HIGH, "High Vulnerability", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_VULN_MED_COUNT = 
		  new Metric.Builder(BDSPluginConstants.VULNERABILITY_MED, "Medium Vulnerability", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public final static Metric CC_VULN_LOW_COUNT = 
		  new Metric.Builder(BDSPluginConstants.VULNERABILITY_LOW, "Low Vulnerability", Metric.ValueType.INT)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  // Json
  public static final Metric VULN_LIST_HIGH_JSON = new Metric.Builder(BDSPluginConstants.VULNERABILITY_HIGH_JSON_KEY, "High Vulnerabilities", Metric.ValueType.DATA)
  .setDescription("JSON list of high vulns")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public static final Metric VULN_LIST_MED_JSON = new Metric.Builder(BDSPluginConstants.VULNERABILITY_MED_JSON_KEY, "Medium Vulnerabilities", Metric.ValueType.DATA)
  .setDescription("JSON list of medium vulns")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  public static final Metric VULN_LIST_LOW_JSON = new Metric.Builder(BDSPluginConstants.VULNERABILITY_LOW_JSON_KEY, "Low Vulnerabilities", Metric.ValueType.DATA)
  .setDescription("JSON list of low vulns")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();
  
  /**
   * Every metric initialized must be added to the list, otherwise Sonar does not pick it up
   */
  
  /**
   * License distribution
   */
  public static final Metric LICENSE_BREAKDOWN_JSON = 
		  new Metric.Builder(BDSPluginConstants.LICENSE_BREAKDOWN_JSON_KEY, "License Breakdown", Metric.ValueType.DISTRIB)
  .setDescription("This is the breakdown of the licenses by conflict")
  .setDirection(Metric.DIRECTION_NONE)
  .setDomain(CoreMetrics.DOMAIN_GENERAL)
  .create();

  
  
  public List<Metric> getMetrics() {
    return Arrays.asList
    	(
    		PROTEX_TOTAL_FILES, 
    		PROTEX_PENDING_FILES,
    		PROTEX_NO_DISCOVERY_FILES,
    		PROTEX_DISCOVERY_FILES,
    		PROTEX_ASSOCIATED_PROJECT,
    		PROTEX_ANALYZED_DATE,
    		PROTEX_LICENSE_CONFLICT_FILES,
    		CC_APP_BOM_URL,
    		CC_REQUESTS_APPROVED,
    		CC_REQUESTS_REJECTED,
    		CC_REQUESTS_PENDING, 
    		CC_REQUESTS_NS,
    		COMP_LIST_APPROVED_JSON,
    		COMP_LIST_PENDING_JSON,
    		COMP_LIST_REJECTED_JSON,
    		COMP_LIST_NS_JSON,
    		CC_VULN_HIGH_COUNT,
    		CC_VULN_MED_COUNT,
    		CC_VULN_LOW_COUNT, 
    		VULN_LIST_HIGH_JSON,
    		VULN_LIST_MED_JSON,
    		VULN_LIST_LOW_JSON,
    		LICENSE_BREAKDOWN_JSON
    		
    	);
  }
}
