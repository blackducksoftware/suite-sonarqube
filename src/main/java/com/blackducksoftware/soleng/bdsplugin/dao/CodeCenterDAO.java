package com.blackducksoftware.soleng.bdsplugin.dao;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import soleng.framework.core.config.ConfigurationManager;
import soleng.framework.standard.codecenter.CodeCenterServerWrapper;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionOrIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.Project;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionOrIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseSummary;
import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestApplicationComponentToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.VulnerabilityApi;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySummary;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginCodeCenterConfigManager;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginProtexConfigManager;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginUser;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.CompPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;
import com.blackducksoftware.soleng.bdsplugin.model.VulnPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO.VULNERABILITY_SEVERITY;


/**
 * All Code Center Connectivity goes through this DAO class
 * @author Ari Kamen
 *
 */
public class CodeCenterDAO extends CommonDAO
{

	static Logger log = LoggerFactory.getLogger(CodeCenterDAO.class.getName());
	
	private static String SERVER = "";
	private static String USER_NAME = "";
	private static String PASSWORD = "";
	
	private static String APP_NAME = "";
	private static String VERSION_ID = "";
	
	// Internal vars
	private ApplicationApi aApi = null;
	private Application app = null;
	private VulnerabilityApi vulApi = null;
	private ColaApi colaApi = null;
	
	// Settings for Sonar
	private Settings settings = null;
	private BDSPluginCodeCenterConfigManager ccConfigManager = null;
	private CodeCenterServerWrapper ccServerWrapper = null;
	
	public CodeCenterDAO(Settings settings, String sonarProjectName) throws Exception
	{
		this.settings = settings;
	
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
		  Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		   // ANY thing that references the SDK
		} finally {
		  Thread.currentThread().setContextClassLoader(original);
		}

		authenticate();
	}

	@Override
	public void authenticate() throws Exception 
	{
		try
		{
			if(settings != null)
			{
				SERVER = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);
				USER_NAME = settings.getString(BDSPluginConstants.PROPERTY_CC_USERNAME);
				PASSWORD = settings.getString(BDSPluginConstants.PROPERTY_CC_PASSSWORD);
				
				APP_NAME = settings.getString(BDSPluginConstants.PROPERTY_CC_PROJECT);
				VERSION_ID = settings.getString(BDSPluginConstants.PROPERTY_CC_VERSION);
		
				
				try{
					checkProperty(SERVER, BDSPluginConstants.PROPERTY_CC_URL);
					checkProperty(USER_NAME, BDSPluginConstants.PROPERTY_CC_USERNAME);
					checkProperty(PASSWORD, BDSPluginConstants.PROPERTY_CC_PASSSWORD);
					checkProperty(APP_NAME, BDSPluginConstants.PROPERTY_CC_PROJECT);
					checkProperty(VERSION_ID, BDSPluginConstants.PROPERTY_CC_VERSION);
				} catch (Exception e)
				{
					throw new SonarException(e);
				}
			}
			
			BDSPluginUser user = new BDSPluginUser(SERVER, USER_NAME, PASSWORD);
			ccConfigManager = new BDSPluginCodeCenterConfigManager(user); 
			
			
			ccConfigManager = (BDSPluginCodeCenterConfigManager) collectGeneralSettings(ccConfigManager, settings);
	            
			
	          // workaround for this here http://fusesource.com/forums/thread.jspa?messageID=10988
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        	ccServerWrapper = new CodeCenterServerWrapper(ccConfigManager);
			log.info("Code Center authentication complete.");
					
			aApi = ccServerWrapper.getInternalApiWrapper().applicationApi;
			vulApi = ccServerWrapper.getInternalApiWrapper().vulnerabilityApi;
			colaApi = ccServerWrapper.getInternalApiWrapper().colaApi;
	
		}
		catch (Throwable e)
		{
			throw new Exception("Could not properly authenticate Code Center, error: " + e.getMessage());
		}
	}

	private void checkProperty(String value, String prop) throws Exception 
	{
		if(value == null || value.length() == 0)
		{
			throw new Exception("The following property cannot be null or empty! " + prop);
		}
		
	}

	public ApplicationPOJO populateApplicationInfo(ApplicationPOJO pojo)
	{	
		try
		{
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
			appToken.setName(APP_NAME);
			appToken.setVersion(VERSION_ID);

			app = aApi.getApplication(appToken);		
			log.info("Found application: " + app.getDescription());
			
			// Get associated Protex project!
			try
			{
				// TODO:  Lookup the user specified project, then derive ID.
				Project associatedProject = aApi.getAssociatedProtexProject(appToken);
				String protexName = associatedProject.getName();
			
				log.info("Found associated project in Protex: " + associatedProject.getName());
				settings.setProperty(BDSPluginConstants.PROPERTY_PROTEX_PROJECT, protexName);
				pojo.setProjectName(protexName);
				pojo.setProjectID(associatedProject.getId().getId());
			
				String associatedServer = associatedProject.getId().getServerId().toString();
				
				
			} catch(SdkFault fault)
			{
				log.error("Unable to get associated project: " + fault.getMessage());
			}					
		} catch (Exception e)
		{
			log.error("Unable to get application object with name: " + APP_NAME);
			log.error("Error: " + e.getMessage());
			pojo.setCcErrorMsg("General error: " + e.getMessage());
			
			return pojo;
		}
		catch (Throwable t)
		{
			log.error("General fatal error", t);
			pojo.setCcErrorMsg("General error: " + t.getMessage());
			return pojo;
		}
		pojo.setAppName(app.getName());
		pojo.setAppVersion(app.getVersion());
		pojo.setAppDescription(app.getDescription());
		pojo.setApplicationId(app.getId());
		
		return pojo;
	}

	/**
	 * Based on the request.getApprovalStatus create buckets for requests
	 * @param pojo
	 * @return
	 */
	public ApplicationPOJO populateComponentBreakdown(ApplicationPOJO pojo) 
	{
		List<CompPOJO> allComponents = new ArrayList<CompPOJO>();
		try
		{
			log.info("Gathering all requests.");
			allComponents = getAllRequestsForApplication(allComponents, pojo, pojo.getApplicationId());
		
			List<CompPOJO> approvedComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> rejectedComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> pendingComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> unknownComponents = new ArrayList<CompPOJO>();
			
			for(CompPOJO component : allComponents)
			{
				// Request Information
				String status = component.getRequestType();				
				switch(status)
				{
					case "APPROVED": 
					{
						approvedComponents.add(component);					
						break;
					}
					case "PENDING": 
					{
						pendingComponents.add(component);
						break;
					}					
					case "REJECTED":
					{	
						rejectedComponents.add(component);
						break;
					}
					case "NOTSUBMITTED":
					{
						unknownComponents.add(component);
						break;
					}
					default: 
					{
						log.warn("Request item not recognized: " + status);
						break;
					}
				
				}
			}
			
			
			pojo.setComponentsApproved(approvedComponents);
			pojo.setComponentsPending(pendingComponents);
			pojo.setComponentsRejected(rejectedComponents);
			pojo.setComponentsUnknown(unknownComponents);

			
		} catch (Exception e)
		{
			log.error("Could not population component pojo for application: " + app.getName());
		}
		
		log.info("Finished collecting application requests");
		return pojo;
	}

	
	/**
	 * Recursively looks through all the requests, if a request is another application
	 * then continues looking until all requests are fetched.
	 * @param requests
	 * @param applicationId
	 * @return
	 */
	private List<CompPOJO> getAllRequestsForApplication(
			List<CompPOJO> allComponents, ApplicationPOJO pojo, ApplicationNameVersionOrIdToken appToken) 
	{
		try
		{	
			List<RequestSummary> summaries = new ArrayList<RequestSummary>();
	
			try{
				summaries = aApi.getApplicationRequests(appToken);
			} catch (SdkFault sdk)
			{
				// We catch here, because of bug CC-10683
				log.warn("Unable to get app information for: " + sdk.getMessage());
				String errorCode = sdk.getFaultInfo().getErrorCode().toString();
				if(errorCode != null && errorCode.contains("NO_APPLICATION_NAMEVERISON_FOUND"))
					log.warn("Most likely a custom component.");
			}
			for(RequestSummary request : summaries)
			{
				CompPOJO component = getComponentData(pojo, request);
				
				// FIXME: We have a SDK bug, where we cannot rely on component's app token.
				// BUG: https://jira/browse/CC-10683
				// So instead we check for kbComponentId and if it is missing, then proceed.	
				//ApplicationIdToken componentAppToken = component.getAppIdToken();
				// If this is not null, then this is not a component, but another CC app. 
				KbComponentIdToken kbToken = component.getKbCompId();
				if(kbToken ==  null)
				{
					// If the KB token is null, then *most* likely we are dealing with another application, although 
					// it can be a custom component too.  This is the workaround.
					ApplicationNameVersionToken componentApplicationToken = new ApplicationNameVersionToken();
					componentApplicationToken.setName(component.getComponentName());
					componentApplicationToken.setVersion(component.getVersion());
					
					getAllRequestsForApplication(allComponents, pojo, componentApplicationToken);
				}
				// Otherwise add it to our list
				allComponents.add(component);
				// Process licenses and vulnerabilities for this component
				populateLicenseData(pojo, request);				
				populateVulnerabilityData(pojo, component, request);
			}
			
		} catch (Exception e)
		{
			log.error("Unable to fetch component for application: " + appToken.toString(), e);
		}
		
		return allComponents;
	}

	/**
	 * Creates a component map, with key being 
	 * @param pojo
	 * @param request
	 */
	private CompPOJO getComponentData(ApplicationPOJO pojo,
			RequestSummary request) 
	{
		CompPOJO componentPojo = null;
		
		String compName = "undefined";
		String compVersion = "undefined";
		try
		{		
			Component comp = colaApi.getCatalogComponent(request.getApplicationComponentToken().getComponentId());
			compName = comp.getName();
			compVersion = comp.getVersion();
			componentPojo = new CompPOJO(compName, compVersion);
			componentPojo.setAppIdToken(comp.getApplicationId());
			componentPojo.setKbCompId(comp.getKbComponentId());
			
			ComponentIdToken token = comp.getId();
			if(token != null)
			{
				componentPojo.setId(comp.getId().getId());
			}
			
			componentPojo.setRequestType(request.getApprovalStatus().toString());
			
			log.info("Found component: " + compName);		
			
			// Generate dynamic URL
			componentPojo = populateUrlForComponent(componentPojo, settings);
			
			log.debug("Component details: " + componentPojo.toString());
			
		} catch (Exception e)
		{
			log.error("Unable to determine component name for request: " + request.toString());
		}
		
		return componentPojo;
		
	}


	/**
	 * Grabs the license information from the SDK and creates the first basic license POJO object.
	 * This object will acquire more data later from Protex.
	 * @param pojo
	 * @param request
	 */
	private void populateLicenseData(ApplicationPOJO pojo,
			RequestSummary request) 
	{
		
		// License Information
		LicenseSummary licSummary = request.getLicenseInfo();
		if(licSummary != null)
		{
			LicenseIdToken licToken = licSummary.getId();
			
			if(licToken != null)
			{
				String lic_id = licToken.getId();
				log.debug("Adding license ID: " + lic_id);
				LicensePOJO licPOJO = new LicensePOJO(lic_id);
			
				pojo.addLicense(licPOJO);
			}
		}
		else
		{
			log.error("Unable to get license summary for request: " + request.toString());
		}
	}

	/**
	 * Populates the application with counts
	 * Populates the internal component bean with vulnerability information
	 * @param appPojo
	 * @param component 
	 * @param request
	 */
	private void populateVulnerabilityData(ApplicationPOJO appPojo, CompPOJO component, RequestSummary request) 
	{
		
		// Attribute/Vulnerability Information
		ComponentNameVersionOrIdToken compIdtoken = request.getApplicationComponentToken().getComponentId();
		VulnerabilityPageFilter filter = new VulnerabilityPageFilter();
		filter.setFirstRowIndex(0);
		filter.setLastRowIndex(Integer.MAX_VALUE);
		filter.setSortAscending(true);
		
		try{
			List<VulnerabilitySummary> vulnerabilities = vulApi.searchDirectMatchedVulnerabilitiesByCatalogComponent(compIdtoken, filter);

			for(VulnerabilitySummary summary : vulnerabilities)
			{
				String severity = summary.getSeverity();
		
				if(severity != null)
				{
					VulnPOJO vuln = new VulnPOJO(summary.getName());			
					vuln.setComments(summary.getComments());
					vuln.setComponentName(component.getComponentName());
					vuln.setComponentVersion(component.getVersion());
					vuln.setDescription(summary.getDescription());
					
					// Trim down the publish date
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					String formattedDate = "";
					try{
						XMLGregorianCalendar gc = summary.getPublished();
						java.util.Date dt = gc.toGregorianCalendar().getTime(); 						
						formattedDate = df.format(dt);
					} catch (Exception e) 
					{ 
						log.debug("Could not convert date", e);
					}
					vuln.setPublishDate(formattedDate);
			
									
					// No enum for severity, have to do this all the old fashioned way :( 
					severity = severity.toLowerCase();
					if(VULNERABILITY_SEVERITY.HIGH.toString().toLowerCase().equals(severity))
					{
						appPojo.addVulnHighList(vuln);
						component.addVulnHigh();
						vuln.setSeverity(VULNERABILITY_SEVERITY.HIGH);
						log.debug("Added high vulnerability: " + vuln.toString());
					}
					else if(VULNERABILITY_SEVERITY.MEDIUM.toString().toLowerCase().equals(severity))
					{
						appPojo.addVulnMedList(vuln);
						component.addVulnMed();
						vuln.setSeverity(VULNERABILITY_SEVERITY.MEDIUM);
						log.debug("Added medium vulnerability: " + vuln.toString());
					}
					else if(VULNERABILITY_SEVERITY.LOW.toString().toLowerCase().equals(severity))
					{
						appPojo.addVulnLowList(vuln);
						component.addVulnLow();
						vuln.setSeverity(VULNERABILITY_SEVERITY.LOW);
						log.debug("Added low vulnerability: " + vuln.toString());
					}
				}
				else
				{
					log.error("No severity information for: " + summary.getName());
				}
				
				log.debug("Vulnerability info: " + summary.getDescription());
				log.debug("Vulnerability severity: " + summary.getSeverity());
			}
		} catch (Exception e)
		{
			log.error("Unable to get vulnerability information for request: " + request.toString());
			log.error("Error: " + e.getMessage(), e);
		}
		
	}

	/**
	 * Constructs a Code Center url using their redirect knowledge
	 * The pattern must follow the protocol:  
	 * Note: Version is optional
	 * http://<servername>/codecenter/CCRedirectPage?isAtTop=true&CCRedirectPageName=Component&CCComponentName=<compName>&CCComponentVersion=<version>
	 * @param pojo
	 * @param settings
	 * @return
	 */
	public CompPOJO populateUrlForComponent(CompPOJO pojo, Settings settings) 
	{
		String server = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);

		if(server != null)
		{
			String compName = pojo.getComponentName();
			String compVersion = pojo.getVersion();
			
			try 
			{
				String urlString = 	
						URIUtil.encodeQuery(server + 
								"/codecenter/CCRedirectPage?isAtTop=true&CCRedirectPageName=Component&CCComponentName=" + compName.trim() + "&CCComponentVersion=" + compVersion);					
				pojo.setUrl(urlString);
								
			} catch (URIException e) 
			{
				log.error("Unable to create a proper url for component: " + compName + " error: " + e.getMessage());
			}	
		}
		else
		{
			log.warn("Unable to construct URLs for components, CC server not specified");
		}
		
		return pojo;
	}

	/**
	 * Populates URLs for specific entry points within Code Center
	 * @param pojo
	 * @param settings
	 * @return
	 */
	public ApplicationPOJO populateURLs(ApplicationPOJO pojo, Settings settings) 
	{
		String server = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);
		String appName = pojo.getAppName();
		String versionName = pojo.getAppVersion();
		// Go through every component in the application
		if(server != null)
		{
			try 
			{
				String urlString = 	
						URIUtil.encodeQuery(server + 
								"codecenter/CCRedirectPage?isAtTop=true&CCRedirectPageName=REquest&CCApplicationName=" + appName + "&CCApplicationVersion=" + versionName);
				
				pojo.setCodeCenterBomPage(urlString);
				
			} catch (URIException e) {
				log.error("Unable to create a proper url for application: " + appName);
			}			
		}
		else
		{
			log.warn("Unable to construct URLs for CC server not specified");
		}
		
		return pojo;
	}
	
}
