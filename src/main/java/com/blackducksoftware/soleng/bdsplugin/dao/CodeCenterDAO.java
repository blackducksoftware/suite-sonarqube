package com.blackducksoftware.soleng.bdsplugin.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.Project;
import com.blackducksoftware.sdk.codecenter.attribute.AttributeApi;
import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributePageFilter;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionOrIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseSummary;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.RequestApi;
import com.blackducksoftware.sdk.codecenter.request.data.Request;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.VulnerabilityApi;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySeverityEnum;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySummary;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginCodeCenterConfigManager;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginUser;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO.VULNERABILITY_SEVERITY;
import com.blackducksoftware.soleng.bdsplugin.model.AttributePOJO;
import com.blackducksoftware.soleng.bdsplugin.model.CompPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;
import com.blackducksoftware.soleng.bdsplugin.model.VulnPOJO;
import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;

/**
 * All Code Center Connectivity goes through this DAO class
 * 
 * @author Ari Kamen
 * 
 */
public class CodeCenterDAO extends CommonDAO {

	static Logger log = LoggerFactory.getLogger(CodeCenterDAO.class.getName());

	private static String SERVER = "";

	private static String USER_NAME = "";

	private static String PASSWORD = "";

	private static String APP_NAME = "";

	private static String VERSION_ID = "";

	// Internal vars
	private Application app = null;

	private ApplicationApi aApi;
	private VulnerabilityApi vulApi;
	private ColaApi colaApi;
	private RequestApi requestApi;

	// Settings for Sonar
	private Settings settings = null;

	private org.sonar.api.resources.Project sonarProject = null;

	private BDSPluginCodeCenterConfigManager ccConfigManager = null;

	private CodeCenterServerWrapper ccServerWrapper = null;

	public CodeCenterDAO(final Settings settings, final org.sonar.api.resources.Project sonarProject) throws Exception {
		this.settings = settings;
		this.sonarProject = sonarProject;

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			authenticate();
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}

	}

	@Override
	public void authenticate() throws Exception {
		try {
			if (settings != null) {
				SERVER = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);
				USER_NAME = settings.getString(BDSPluginConstants.PROPERTY_CC_USERNAME);
				PASSWORD = settings.getString(BDSPluginConstants.PROPERTY_CC_PASSSWORD);

				APP_NAME = settings.getString(BDSPluginConstants.PROPERTY_CC_PROJECT);
				VERSION_ID = settings.getString(BDSPluginConstants.PROPERTY_CC_VERSION);

				try {
					checkProperty(SERVER, BDSPluginConstants.PROPERTY_CC_URL);
					checkProperty(USER_NAME, BDSPluginConstants.PROPERTY_CC_USERNAME);
					checkProperty(PASSWORD, BDSPluginConstants.PROPERTY_CC_PASSSWORD);
					checkOptionalProperty(APP_NAME, BDSPluginConstants.PROPERTY_CC_PROJECT);
					checkOptionalProperty(VERSION_ID, BDSPluginConstants.PROPERTY_CC_VERSION);
				} catch (Exception e) {
					throw new SonarException(e);
				}
			}

			BDSPluginUser user = new BDSPluginUser(SERVER, USER_NAME, PASSWORD);
			ccConfigManager = new BDSPluginCodeCenterConfigManager(user, APPLICATION.CODECENTER);
			ccConfigManager = (BDSPluginCodeCenterConfigManager) collectGeneralSettings(ccConfigManager, settings);

			// If app or version is blank, default it.
			if (APP_NAME == null || APP_NAME.length() == 0) {
				log.warn("Defaulting application name to: " + sonarProject.getName());
				APP_NAME = sonarProject.getName();
			}
			if (VERSION_ID == null || VERSION_ID.length() == 0) {
				log.warn("Defaulting version to: " + sonarProject.getAnalysisVersion());
				VERSION_ID = sonarProject.getAnalysisVersion();
			}

			// workaround for this here
			// http://fusesource.com/forums/thread.jspa?messageID=10988
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ccServerWrapper = new CodeCenterServerWrapper(ccConfigManager);
			log.info("Code Center authentication complete.");

			aApi = ccServerWrapper.getInternalApiWrapper().getApplicationApi();
			vulApi = ccServerWrapper.getInternalApiWrapper().getVulnerabilityApi();
			colaApi = ccServerWrapper.getInternalApiWrapper().getColaApi();
			requestApi = ccServerWrapper.getInternalApiWrapper().getRequestApi();

		} catch (Throwable e) {
			throw new Exception("Could not properly authenticate Code Center, error: " + e.getMessage(), e);
		}
	}

	private void checkProperty(final String value, final String prop) throws Exception {
		if (value == null || value.length() == 0) {
			throw new Exception("The following property cannot be null or empty! " + prop);
		}

	}

	private void checkOptionalProperty(final String value, final String prop) throws Exception {
		if (value == null || value.length() == 0) {
			log.warn("The following is set, defaulting! " + prop);
		}

	}

	public ApplicationPOJO populateApplicationInfo(final ApplicationPOJO pojo) {
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
			appToken.setName(APP_NAME);
			appToken.setVersion(VERSION_ID);

			app = aApi.getApplication(appToken);
			log.info("Found application: " + app.getName() + "  " + app.getVersion() + "(" + app.getDescription() + ")");

			// Get associated Protex project!
			try {
				// TODO: Lookup the user specified project, then derive ID.
				Project associatedProject = aApi.getAssociatedProtexProject(appToken);
				String protexName = associatedProject.getName();

				log.info("Found associated project in Protex: " + associatedProject.getName());
				settings.setProperty(BDSPluginConstants.PROPERTY_PROTEX_PROJECT, protexName);
				pojo.setProjectName(protexName);
				pojo.setProjectID(associatedProject.getId().getId());

				String associatedServer = associatedProject.getId().getServerId().toString();

			} catch (SdkFault fault) {
				log.warn("Unable to get associated project: " + fault.getMessage());
			}
		} catch (Exception e) {
			log.error("Unable to get application object with name: " + APP_NAME);
			log.error("Error: " + e.getMessage());
			pojo.setCcErrorMsg("General error: " + e.getMessage());

			return pojo;
		} catch (Throwable t) {
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
	 * 
	 * @param pojo
	 * @return
	 */
	public ApplicationPOJO populateComponentBreakdown(final ApplicationPOJO pojo) {
		try {
			log.info("Gathering all requests for :" + pojo.getApplicationId().getId());
			List<CompPOJO> allComponents = getAllRequestsForApplication(pojo, pojo.getApplicationId());

			List<CompPOJO> approvedComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> rejectedComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> pendingComponents = new ArrayList<CompPOJO>();
			List<CompPOJO> unknownComponents = new ArrayList<CompPOJO>();

			for (CompPOJO component : allComponents) {
				// Request Information
				String status = component.getRequestType();
				switch (status) {
				case "APPROVED": {
					approvedComponents.add(component);
					break;
				}
				case "PENDING": {
					pendingComponents.add(component);
					break;
				}
				case "REJECTED": {
					rejectedComponents.add(component);
					break;
				}
				case "NOTSUBMITTED": {
					unknownComponents.add(component);
					break;
				}
				default: {
					log.warn("Request item not recognized: " + status);
					break;
				}

				}
			}

			pojo.setComponentsApproved(approvedComponents);
			pojo.setComponentsPending(pendingComponents);
			pojo.setComponentsRejected(rejectedComponents);
			pojo.setComponentsUnknown(unknownComponents);

		} catch (Exception e) {
			log.error("Could not population component pojo for application: " + app.getName());
		}

		log.info("Finished collecting application requests");
		return pojo;
	}

	/**
	 * Recursively looks through all the requests, if a request is another
	 * application then continues looking until all requests are fetched.
	 * 
	 * TODO: Check for nested applications after 7.x refactor
	 * 
	 * @param requests
	 * @param applicationId
	 * @return
	 */
	private List<CompPOJO> getAllRequestsForApplication(final ApplicationPOJO pojo, final ApplicationIdToken appToken) {
		List<CompPOJO> allComponents = new ArrayList<CompPOJO>();
		try {
			// List<RequestSummary> summaries = new ArrayList<RequestSummary>();
			List<RequestPojo> requests = new ArrayList<RequestPojo>();

			try {
				// summaries = aApi.getApplicationRequests(appToken);
				requests = this.ccServerWrapper.getApplicationManager().getRequestsByAppId(appToken.getId());
			} catch (CommonFrameworkException cfe) {
				throw new Exception("Unable to get requests!", cfe);
			}
			if (requests.size() == 0) {
				log.warn("No Requests for " + pojo.getAppName() + "  " + pojo.getAppVersion());
			}
			for (RequestPojo requestPojo : requests) {

				Request request;
				try {
					RequestIdToken requestToken = new RequestIdToken();
					requestToken.setId(requestPojo.getRequestId());
					request = requestApi.getRequest(requestToken);

					CompPOJO component = getComponentData(pojo, request);

					// If the component has an app token, this is a rolled up
					// app and requires us to dive inside.
					ApplicationIdToken componentAppToken = component.getAppIdToken();
					if (componentAppToken != null) {
						allComponents.addAll(getAllRequestsForApplication(pojo, componentAppToken));
					}
					// Otherwise add it to our list
					allComponents.add(component);
					log.info("Fetched component: " + component.getComponentName() + "  " + component.getVersion());
					// Process licenses and vulnerabilities for this component
					populateLicenseData(pojo, request);
					populateVulnerabilityData(pojo, component, request);
				} catch (SdkFault e) {
					log.error("Error getting request, for id: " + requestPojo.getRequestId());
				}
			}

		} catch (Exception e) {
			log.error("Unable to fetch component for application: " + appToken.toString(), e);
		}

		return allComponents;
	}

	/**
	 * Creates a component map, with key being
	 * 
	 * @param pojo
	 * @param request
	 */
	private CompPOJO getComponentData(final ApplicationPOJO pojo, final RequestSummary request) {
		CompPOJO componentPojo = null;

		String compName = "undefined";
		String compVersion = "undefined";
		try {
			Component comp = colaApi.getCatalogComponent(request.getApplicationComponentToken().getComponentId());
			compName = comp.getName();
			compVersion = comp.getVersion();
			componentPojo = new CompPOJO(compName, compVersion);
			componentPojo.setAppIdToken(comp.getApplicationId());
			componentPojo.setKbCompId(comp.getKbComponentId());

			ComponentIdToken token = comp.getId();
			if (token != null) {
				componentPojo.setId(comp.getId().getId());
			}

			componentPojo.setRequestType(request.getApprovalStatus().toString());

			log.info("Found component: " + compName);

			// Generate dynamic URL
			componentPojo = populateUrlForComponent(componentPojo, settings);

			log.debug("Component details: " + componentPojo.toString());

		} catch (Exception e) {
			log.error("Unable to determine component name for request: " + request.toString());
		}

		return componentPojo;

	}

	/**
	 * Grabs the license information from the SDK and creates the first basic
	 * license POJO object. This object will acquire more data later from
	 * Protex.
	 * 
	 * @param pojo
	 * @param request
	 */
	private void populateLicenseData(final ApplicationPOJO pojo, final RequestSummary request) {

		// License Information
		LicenseSummary licSummary = request.getLicenseInfo();
		if (licSummary != null) {
			LicenseIdToken licToken = licSummary.getId();

			if (licToken != null) {
				String lic_id = licToken.getId();
				log.debug("Adding license ID: " + lic_id);
				LicensePOJO licPOJO = new LicensePOJO(lic_id);

				pojo.addLicense(licPOJO);
			}
		} else {
			log.error("Unable to get license summary for request: " + request.toString());
		}
	}

	/**
	 * Populates the application with counts Populates the internal component
	 * bean with vulnerability information
	 * 
	 * @param appPojo
	 * @param component
	 * @param request
	 */
	private void populateVulnerabilityData(final ApplicationPOJO appPojo, final CompPOJO component,
			final RequestSummary request) {

		// Attribute/Vulnerability Information
		ComponentNameVersionOrIdToken compIdtoken = request.getApplicationComponentToken().getComponentId();
		VulnerabilityPageFilter filter = new VulnerabilityPageFilter();
		filter.setFirstRowIndex(0);
		filter.setLastRowIndex(Integer.MAX_VALUE);
		filter.setSortAscending(true);

		try {
			List<VulnerabilitySummary> vulnerabilities = vulApi.searchDirectMatchedVulnerabilitiesByCatalogComponent(
					compIdtoken, filter);

			for (VulnerabilitySummary summary : vulnerabilities) {
				VulnerabilitySeverityEnum severityEnum = summary.getSeverity();

				if (severityEnum != null) {
					VulnPOJO vuln = new VulnPOJO(summary.getName().toString());
					// AK - Comments are gone in 7.x
					// vuln.setComments(summary.getComments());
					vuln.setComponentName(component.getComponentName());
					vuln.setComponentVersion(component.getVersion());
					vuln.setDescription(summary.getDescription());

					// Trim down the publish date
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					String formattedDate = "";
					try {
						Date publishedDate = summary.getPublished();
						formattedDate = df.format(publishedDate);
					} catch (Exception e) {
						log.debug("Could not convert date", e);
					}
					vuln.setPublishDate(formattedDate);

					if (severityEnum == VulnerabilitySeverityEnum.HIGH) {
						appPojo.addVulnHighList(vuln);
						component.addVulnHigh();
						vuln.setSeverity(VULNERABILITY_SEVERITY.HIGH);
						log.debug("Added high vulnerability: " + vuln.toString());
					} else if (severityEnum == VulnerabilitySeverityEnum.MEDIUM) {
						appPojo.addVulnMedList(vuln);
						component.addVulnMed();
						vuln.setSeverity(VULNERABILITY_SEVERITY.MEDIUM);
						log.debug("Added medium vulnerability: " + vuln.toString());
					} else if (severityEnum == VulnerabilitySeverityEnum.LOW) {
						appPojo.addVulnLowList(vuln);
						component.addVulnLow();
						vuln.setSeverity(VULNERABILITY_SEVERITY.LOW);
						log.debug("Added low vulnerability: " + vuln.toString());
					}
				} else {
					log.error("No severity information for: " + summary.getName());
				}

				log.debug("Vulnerability info: " + summary.getDescription());
				log.debug("Vulnerability severity: " + summary.getSeverity());
			}
		} catch (Exception e) {
			log.error("Unable to get vulnerability information for request: " + request.toString());
			log.error("Error: " + e.getMessage(), e);
		}

	}

	/**
	 * Constructs a Code Center url using their redirect knowledge The pattern
	 * must follow the protocol: Note: Version is optional
	 * http://<servername>/codecenter
	 * /CCRedirectPage?isAtTop=true&CCRedirectPageName
	 * =Component&CCComponentName=<compName >&CCComponentVersion=<version>
	 * 
	 * @param pojo
	 * @param settings
	 * @return
	 */
	public CompPOJO populateUrlForComponent(final CompPOJO pojo, final Settings settings) {
		String server = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);

		if (server != null) {
			String compName = pojo.getComponentName();
			String compVersion = pojo.getVersion();

			try {
				if (!server.endsWith("/")) {
					server += "/";
				}
				String urlString = URIUtil.encodeQuery(server
						+ "codecenter/CCRedirectPage?isAtTop=true&CCRedirectPageName=Component&CCComponentName="
						+ compName.trim() + "&CCComponentVersion=" + compVersion);
				pojo.setUrl(urlString);

			} catch (URIException e) {
				log.error("Unable to create a proper url for component: " + compName + " error: " + e.getMessage());
			}
		} else {
			log.warn("Unable to construct URLs for components, CC server not specified");
		}

		return pojo;
	}

	/**
	 * Populates URLs for specific entry points within Code Center
	 * 
	 * @param pojo
	 * @param settings
	 * @return
	 */
	public ApplicationPOJO populateURLs(final ApplicationPOJO pojo, final Settings settings) {
		String server = settings.getString(BDSPluginConstants.PROPERTY_CC_URL);
		String appName = pojo.getAppName();
		String versionName = pojo.getAppVersion();
		// Go through every component in the application
		if (server != null) {
			try {
				StringBuilder urlBuilder = new StringBuilder(server);
				if (!server.endsWith("/")) {
					urlBuilder.append("/");
				}

				urlBuilder.append("codecenter");
				// FIXME CC-13128
				/*
				 * Hack!!! Dangerous workaround for CC-13128 This assumes that
				 * the components are already filled in the Pojo, which we
				 * ensure in the calling method
				 */
				CompPOJO firstComp = getFirstComponent(pojo);
				if (firstComp != null) {
					log.warn("**Hack** adding false selection to CC BOM URL");
					urlBuilder.append("/CCRedirectPage?isAtTop=true&CCRedirectPageName=Request&CCApplicationName=")
							.append(appName).append("&CCApplicationVersion=").append(versionName)
							.append("&CCComponentName=").append(firstComp.getComponentName())
							.append("&CCComponentVersion=").append(firstComp.getVersion());
				} else {
					//
					log.warn("CC URL will fail, because there are no components");
					log.warn("Sending the user to the CC Server URL page instead");
					// when CC-13128 is fixed the Re-direct URL for the
					// application
					// BOM will go here.
				}
				/*
				 * End of Hack
				 */
				String urlString = URIUtil.encodeQuery(urlBuilder.toString());
				pojo.setCodeCenterBomPage(urlString);

			} catch (URIException e) {
				log.error("Unable to create a proper url for application: " + appName);
			}
		} else {
			log.warn("Unable to construct URLs for CC server not specified");
		}

		return pojo;
	}

	private CompPOJO getFirstComponent(final ApplicationPOJO pojo) {
		List<CompPOJO> compsA = pojo.getComponentsApproved();
		if (compsA.size() > 0) {
			log.debug("Approved: " + compsA.size());
			return compsA.get(0);
		}
		List<CompPOJO> compsR = pojo.getComponentsRejected();
		if (compsR.size() > 0) {
			log.debug("Rejected: " + compsR.size());
			return compsR.get(0);
		}
		List<CompPOJO> compsP = pojo.getComponentsPending();
		if (compsP.size() > 0) {
			log.debug("Pending: " + compsP.size());
			return compsP.get(0);
		}
		List<CompPOJO> compsU = pojo.getComponentsUnknown();
		if (compsU.size() > 0) {
			log.debug("Unknown: " + compsU.size());
			return compsU.get(0);
		}
		log.debug("No components");
		return null;
	}

	public ApplicationPOJO collectCustomAttributes(final ApplicationPOJO applicationPojo) {
		AttributeApi attributeApi = ccServerWrapper.getInternalApiWrapper().getAttributeApi();

		try {
			AttributePageFilter filter = new AttributePageFilter();
			filter.setFirstRowIndex(0);
			filter.setLastRowIndex(Integer.MAX_VALUE);
			List<AttributeValue> attributes = app.getAttributeValues();

			if (attributes != null) {
				for (AttributeValue attValue : attributes) {
					AbstractAttribute attribute = null;
					try {
						AttributeNameOrIdToken id = attValue.getAttributeId();
						attribute = attributeApi.getAttribute(id);
						log.debug("Examining attribute: " + attribute.getQuestion());

						// We want to store the question and the answer.
						// This is a bit kludgy, but the SDK is forcing us into
						// this predicament.
						// Because the actual Attribute does not contain the
						// "answer", we must rely on the list of
						// values that we initially grabbed.
						// TODO: Although a list comes back, it never appears to
						// actually include a list - so we get the
						// first index.
						AttributePOJO attPojo = new AttributePOJO(attribute.getQuestion(), attValue.getValues().get(0));
						applicationPojo.addAttributes(attPojo);
					} catch (Exception e) {
						log.error("Unable to get attribute details for: " + attribute.getName());
					}
				}

				log.debug("Found number of attributes: " + attributes.size());
			} else {
				log.info("No attributes available for this application");
			}

		} catch (Exception e) {
			log.error("Unable to get attributes", e);
		}

		return applicationPojo;
	}

}
