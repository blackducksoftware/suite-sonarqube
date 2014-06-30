package com.blackducksoftware.soleng.bdsplugin;

import java.util.Date;



import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.google.gson.Gson;



public class BDSPluginSensor implements Sensor {

	private static Logger log = LoggerFactory.getLogger(BDSPluginSensor.class.getName());
	
	private ApplicationPOJO pojo =  new ApplicationPOJO();
	private Settings settings = null;
	
	public static void main(String[] args) 
	{
		Settings sets = new Settings();
		sets.appendProperty(BDSPluginConstants.PROPERTY_CC_PROJECT, "Ari_App");
		sets.appendProperty(BDSPluginConstants.PROPERTY_CC_VERSION, "1.0");
		
		sets.appendProperty(BDSPluginConstants.PROPERTY_PROTEX_URL, "https://kiowa.blackducksoftware.com");
		sets.appendProperty(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD, "blackduck");
		sets.appendProperty(BDSPluginConstants.PROPERTY_PROTEX_USERNAME, "akamen@blackducksoftware.com");
		
		sets.appendProperty(BDSPluginConstants.PROPERTY_CC_URL, "http://satemplatecc1");
	
	
		BDSPluginSensor sensor = new BDSPluginSensor(sets);	
		
		sensor.analyse(null, null);
	}
	
	/**
	 * Optional constructor if you want to utilize the properties set forth by the Plugin class
	 * Settings are
	 * @param configuration
	 */
	public BDSPluginSensor(Settings settings) 
	{
		this.settings = settings;	
	}
	
	public boolean shouldExecuteOnProject(Project arg0) 
	{
		return true;
	}

	public void analyse(Project sonarProject, SensorContext sensorContext) 
	{
	
		CodeCenterConnector ccConecctor = null;
		ProtexConnector protexConnector = null;
	
		// Code Center should be initialized first so that it can get associated project info.
		// Catch authentication exceptions and store the errors.
		try{
			ccConecctor = new CodeCenterConnector(settings, sonarProject);
		} catch (Exception e)
		{
			log.error("Unable to authenticate Code Center, cause: " + e.getMessage());
			pojo.setCcErrorMsg(e.getMessage());
		}
		try{
			
			protexConnector = new ProtexConnector(settings, sonarProject);
		} catch (Exception e)
		{
			log.error("Unable to authenticate Protex, cause: " + e.getMessage());
			pojo.setProtexErrorMsg(e.getMessage());
		}
		
		
		// Get the basic stuff
		if(ccConecctor != null)
			pojo = ccConecctor.populateApplicationPojo(pojo);
		
		if(protexConnector != null)
		{
			pojo = protexConnector.populateApplicationWithProtexData(pojo, sensorContext);
		
			// Determine license breakdown
			pojo = protexConnector.populateLicenseData(pojo);
		}
		
		log.info("Finished: " + pojo.toString());

		saveMetrics(pojo, sensorContext);
		
	}

	/**
	 * Save all the application pojo metrics inside the Sonar DB
	 * @param pojo
	 * @param sensorContext
	 */
	private void saveMetrics(ApplicationPOJO pojo, SensorContext sensorContext) 
	{	
		try{
			// Errors if any
			saveMetricString(sensorContext, pojo.getProtexErrorMsg(), BDSPluginMetrics.PROTEX_ERROR_MESSAGE);	
			saveMetricString(sensorContext, pojo.getCcErrorMsg(), BDSPluginMetrics.CC_ERROR_MESSAGE);	
			
			// Protex data
			saveMetricInt(sensorContext, pojo.getTotalFileCount(), BDSPluginMetrics.PROTEX_TOTAL_FILES);
			saveMetricInt(sensorContext, pojo.getTotalPendingFileCount(), BDSPluginMetrics.PROTEX_PENDING_FILES);
			saveMetricInt(sensorContext, pojo.getTotalNoDiscoveryCount(), BDSPluginMetrics.PROTEX_NO_DISCOVERY_FILES);	
			saveMetricInt(sensorContext, pojo.getTotalDiscoveryCount(), BDSPluginMetrics.PROTEX_DISCOVERY_FILES);	
			saveMetricInt(sensorContext, pojo.getTotalLicenseConflictCount(), BDSPluginMetrics.PROTEX_LICENSE_CONFLICT_FILES);	
			saveMetricString(sensorContext, pojo.getProjectName(), BDSPluginMetrics.PROTEX_ASSOCIATED_PROJECT);	
			saveMetricString(sensorContext, pojo.getDateLastAnalyzed(), BDSPluginMetrics.PROTEX_ANALYZED_DATE);	
			saveMetricJson(sensorContext, pojo.getBDSProtexInfo(), BDSPluginMetrics.PROTEX_INFO_JSON);	
			
			/*
			 * REQUESTS
			 * */
			saveMetricString(sensorContext, pojo.getCodeCenterBomPage(), BDSPluginMetrics.CC_APP_BOM_URL);	
			// Component Request Measures
			saveMetricInt(sensorContext, pojo.getApprovedComponentCount(), BDSPluginMetrics.CC_REQUESTS_APPROVED);
			saveMetricInt(sensorContext, pojo.getPendingComponentCount(), BDSPluginMetrics.CC_REQUESTS_PENDING);
			saveMetricInt(sensorContext, pojo.getRejectedComponentCount(), BDSPluginMetrics.CC_REQUESTS_REJECTED);
			saveMetricInt(sensorContext, pojo.getUnknownComponentCount(), BDSPluginMetrics.CC_REQUESTS_NS);
			// Component Request JSON
			saveMetricJson(sensorContext, pojo.getComponentsApproved(), BDSPluginMetrics.COMP_LIST_APPROVED_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsPending(), BDSPluginMetrics.COMP_LIST_PENDING_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsRejected(), BDSPluginMetrics.COMP_LIST_REJECTED_JSON);
			saveMetricJson(sensorContext, pojo.getComponentsUnknown(), BDSPluginMetrics.COMP_LIST_NS_JSON);
			// Custom Attributes
			saveMetricJson(sensorContext, pojo.getAttributes(), BDSPluginMetrics.CC_CUSTOM_ATTIBUTES_JSON);
			
			
			/*
			 * VULNERABILITIES
			 * */
			
			// CC Vulnerabilities
			saveMetricInt(sensorContext, pojo.getVulnHighCount(), BDSPluginMetrics.CC_VULN_HIGH_COUNT);
			saveMetricInt(sensorContext, pojo.getVulnMedCount(), BDSPluginMetrics.CC_VULN_MED_COUNT);
			saveMetricInt(sensorContext, pojo.getVulnLowCount(), BDSPluginMetrics.CC_VULN_LOW_COUNT);
			
			// Vulnerabilities JSON
			saveMetricJson(sensorContext, pojo.getVulnHighList(), BDSPluginMetrics.VULN_LIST_HIGH_JSON);
			saveMetricJson(sensorContext, pojo.getVulnMedList(), BDSPluginMetrics.VULN_LIST_MED_JSON);
			saveMetricJson(sensorContext, pojo.getVulnLowList(), BDSPluginMetrics.VULN_LIST_LOW_JSON);
			
			// Licenses for the chart
			saveMetricJson(sensorContext, pojo.getSortedLicenseMap(), BDSPluginMetrics.LICENSE_BREAKDOWN_JSON);
			
		} catch (Exception e)
		{
			log.warn("Unable to save all metrics!", e);
		}
	}
	


	private void saveMetricDate(SensorContext sensorContext,
			Date value, Metric metric) 
	{
		final Measure measure = new Measure(metric);
		measure.setDate(value);
		
		if(sensorContext != null)
			sensorContext.saveMeasure(measure);
		
		log.info("Saved new measure: " + measure.toString());	
	}
	
	private void saveMetricString(SensorContext sensorContext,
			String value, Metric metric) 
	{
		try{
			final Measure measure = new Measure(metric);
			measure.setData(value);
			
			measure.setPersistenceMode(PersistenceMode.DATABASE);
			
			if(sensorContext != null)
				sensorContext.saveMeasure(measure);
			
			log.info("Saved new measure: " + measure.toString());	
		
		} catch (Exception e)
		{
			log.warn("Unable to save metric: " + metric.getName(), e);
		}
		
	}


	/**
	 * Converts the objects into a JSON string and saved that as the metric
	 * @param sensorContext
	 * @param sortedLicenseMap
	 * @param licenseBreakdownJson
	 */
	private void saveMetricJson(SensorContext sensorContext,
			Object collection,
			Metric licenseBreakdownJson) 
	{
		Gson gson = new Gson();
		
		String value = gson.toJson(collection);
		
		final Measure measure = new Measure(licenseBreakdownJson);
		measure.setData(value);
		// TODO: Investigate whether this is the best approach.
		//measure.setPersistenceMode(PersistenceMode.DATABASE);
		
		if(sensorContext != null)
			sensorContext.saveMeasure(measure);
		
		log.info("Saved new measure: " + measure.toString());	
		
	}
	
	private void saveMetricInt(SensorContext sensorContext, Integer value, Metric metric)
	{
		final Measure measure = new Measure(metric);
		measure.setIntValue(value);
		
		if(sensorContext != null)
			sensorContext.saveMeasure(measure);
		
		log.info("Saved new measure: " + measure.toString());
	}

}
