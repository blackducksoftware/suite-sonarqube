package com.blackducksoftware.soleng.bdsplugin.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_3;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.license.LicenseAttributes;
import com.blackducksoftware.sdk.protex.license.LicenseExtensionLevel;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.bom.FileCountType;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeWithCount;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTreeWithCount;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.DiscoveryApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeTreeIdentificationInfo;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginMetrics;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;

public class ProtexDAO implements SDKDAO 
{
	static Logger log = LoggerFactory.getLogger(ProtexDAO.class.getName());
	
	private static String SERVER = "";
	private static String USER_NAME = "";
	private static String PASSWORD = "";
	private static String PROJECT_NAME = "";
	
	private ProtexServerProxyV6_3 proxy = null;
	private ProjectApi projectApi = null;
	private CodeTreeApi codeTreeApi = null;
	private DiscoveryApi discoveryApi = null;
	private LicenseApi licenseApi = null;
	private IdentificationApi idApi = null;
	private BomApi bomApi = null;
	
	private Settings settings = null;
	
	
	public ProtexDAO(Settings settings, String sonarProjectName)
	{
		this.settings = settings;
		authenticate();		
	}

	public void authenticate() {
		try
		{
			SERVER = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_URL);
			USER_NAME = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_USERNAME);
			PASSWORD = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD);
			PROJECT_NAME = settings.getString(BDSPluginConstants.PROPERTY_PROTEX_PROJECT);
			settings.appendProperty(BDSPluginConstants.PROPERTY_PROTEX_PROJECT, "AT Project");	
			if(SERVER == null || SERVER.length() == 0)
			{
				log.error("Server url must be specificed! Exiting");
			}
			
	          // workaround for this here http://fusesource.com/forums/thread.jspa?messageID=10988
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			proxy = new ProtexServerProxyV6_3(SERVER, USER_NAME, PASSWORD);
				
			projectApi = proxy.getProjectApi();
			codeTreeApi = proxy.getCodeTreeApi();
			discoveryApi = proxy.getDiscoveryApi();
			licenseApi = proxy.getLicenseApi();
			idApi = proxy.getIdentificationApi();
			bomApi = proxy.getBomApi();
			
			log.info("Protex authentication completed");
		} catch (Exception e)
		{
			log.error("Authentication failure: " + e.getMessage()); 
		}
		
	}

	public void populateProjectInfo(ApplicationPOJO pojo) 
	{
		try{
			// Check to see if the pojo contains populated information
			String pojoProjectName = pojo.getProjectName();
			if(pojoProjectName != null && pojoProjectName.length() > 0)
			{
				PROJECT_NAME = pojoProjectName;
			}
			
			// 
			if(PROJECT_NAME == null || PROJECT_NAME.length() == 0)
			{
				log.error("Nothing to analyze, project name is empty");
				return;
			}
			
			log.info("Getting project information for project name: " + PROJECT_NAME);
			
			Project project = projectApi.getProjectByName(PROJECT_NAME);
			
			pojo.setProjectID(project.getProjectId());
			pojo.setProjectName(project.getName());
			
			// Format last analyzed date
			Date lastAnalyzed = project.getLastAnalyzedDate();
			DateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
			String prettyDate = df.format(lastAnalyzed);
			
			pojo.setDateLastAnalyzed(prettyDate);
			
			// Grab the latest refresh date
			getRefreshDateFromSonar(pojo);
			
		} catch (Exception e)
		{
			log.error("Could not get project information for: " + PROJECT_NAME);
			log.error("Error: " + e.getMessage());
		}
		
		log.info("Got project information for: " + PROJECT_NAME);
	}

	private ApplicationPOJO getRefreshDateFromSonar(ApplicationPOJO pojo) {
		
		String refreshDateString= settings.getString(BDSPluginConstants.PROPERTY_PROTEX_REFRESH_BOM_DATE);
		
		Date date = null;
		if(refreshDateString != null)
		{		
			try {
				date = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).parse(refreshDateString);
			} catch (ParseException e) {
			}
		}
	
		pojo.setLastrefreshDate(date);

		return pojo;
	}
	
	private Date getRefreshDateFromProtex(ApplicationPOJO pojo) {
		
		Date lastRefreshDate = null;
			
		try {
			lastRefreshDate = bomApi.getLastBomRefreshFinishDate(pojo.getProjectID());
		} catch (SdkFault e) {
			log.warn("Unable to get or set refresh date", e);
		}
	
		return lastRefreshDate;
	}

	public void populateProjectFileCounts(ApplicationPOJO pojo, SensorContext sensorContext)
	{
		// Grab the refresh date and see if we need to do anything.
		try
		{
			Date sonarRefreshDate = pojo.getLastrefreshDate();
			Date currentRefreshDate = null;
			
			if(sonarRefreshDate == null)
			{
				currentRefreshDate = getRefreshDateFromProtex(pojo);
			}
			else
			{			
				// Compare the dates and if the stored is the same as current, return, otherwise keep going
				int comparison = sonarRefreshDate.compareTo(currentRefreshDate);
				if(comparison >= 0)
				{
					log.info("The last stored refresh date is the same or equal as the current, skipping Protex analysis");
					return;
				}
			}
			
			// If we make it down here, then update the refresh date.
			// FIXME This is currently not saving at all.
			settings.setProperty(BDSPluginConstants.PROPERTY_PROTEX_REFRESH_BOM_DATE, currentRefreshDate.toString());
			
		} catch (Exception e)
		{
			log.warn("Tried to look up refresh date, failed: " + e.getMessage());
		}
		
		Integer totalFiles = new Integer(0);
		Integer totalPendingFiles = new Integer(0);
		Integer skippedFiles = new Integer(0);
		Integer noDiscoveryCount = new Integer(0);
		Integer discoveryCount = new Integer(0);
		Integer idCount = new Integer(0);
		Integer licenseConflictCount = new Integer(0);
		
		PartialCodeTree partialTree = null;
	
			
		try
		{
			partialTree = codeTreeApi.getCodeTree(pojo.getProjectID(), "/", 0, true); 

			PartialCodeTreeWithCount pctAnalyzedFileCount = codeTreeApi.getAnalyzedFileCount(pojo.getProjectID(), partialTree);
			CodeTreeNodeWithCount nodeCount = pctAnalyzedFileCount.getNodes().get(0);
			totalFiles = nodeCount.getCount();
			
			PartialCodeTreeWithCount skippedPctFileCount = codeTreeApi.getSkippedFileCount(pojo.getProjectID(), partialTree);
			nodeCount = skippedPctFileCount.getNodes().get(0);
			skippedFiles = nodeCount.getCount();
			
			PartialCodeTreeWithCount pctLicConflict = bomApi.getFileCount(pojo.getProjectID(), partialTree, FileCountType.LICENSE_CONFLICTS);
			nodeCount = pctLicConflict.getNodes().get(0);
			licenseConflictCount = nodeCount.getCount();

			
		} catch(Exception e)
		{
			log.error("Error getting total project count: " + e.getMessage());
		}
		
		try
		{
	
			PartialCodeTreeWithCount pctFileCount  = discoveryApi.getAllDiscoveriesPendingIdFileCount(pojo.getProjectID(), partialTree);
			CodeTreeNodeWithCount nodeCount = pctFileCount.getNodes().get(0);
			totalPendingFiles = nodeCount.getCount();
			
			PartialCodeTreeWithCount pctNoDiscoveryCount  = discoveryApi.getNoDiscoveriesFileCount(pojo.getProjectID(), partialTree);
			CodeTreeNodeWithCount noDiscoveries = pctNoDiscoveryCount.getNodes().get(0);
			noDiscoveryCount = noDiscoveries.getCount();
			
		
						
		} catch(Exception e)
		{
			log.error("Error getting pending file count: " + e.getMessage());
		}
		
		pojo.setTotalFileCount(totalFiles);
		pojo.setTotalPendingFileCount(totalPendingFiles);
		pojo.setTotalSkippedFileCount(skippedFiles);
		pojo.setTotalNoDiscoveryCount(noDiscoveryCount);
		pojo.setTotalLicenseConflictCount(licenseConflictCount);
		
		// This is the only value we calculate
		// But we must do it here and then store it as a metric to get Sonar trending.
		discoveryCount = totalFiles - noDiscoveryCount;
		pojo.setTotalDiscoveryCount(discoveryCount);
		
	}

	public LicensePOJO populateLicenseAttributes(LicensePOJO licensePojo) 
	{
		String lic_id = licensePojo.getLicenseID();
		try
		{
			GlobalLicense lic = licenseApi.getLicenseById(lic_id);
			LicenseAttributes attributes = lic.getAttributes();
			LicenseExtensionLevel licLevel = attributes.getIntegrationLevelForLicenseApplication();
			
			String licenseReachName = licLevel.toString();
			if(licenseReachName == null)
				log.warn("Could not get license level reach name for license ID: " + lic_id);
			else
				licensePojo.setLicenseReachString(licenseReachName);
	
		
			Integer ordinalNumber = licLevel.ordinal();
			if(ordinalNumber == null)
				log.warn("Could not get ordinal value for license ID: " + lic_id);
			else
				licensePojo.setLicenseReachNumber(ordinalNumber);
			
		} catch(Exception e)
		{
			log.error("Unable to get license attribuate for license ID: " + lic_id);
			log.error("Error: " + e.getMessage());
		}
		
		return licensePojo;	
	}
	
}
