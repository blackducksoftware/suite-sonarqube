package com.blackducksoftware.soleng.bdsplugin.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;

import soleng.framework.core.exception.CommonFrameworkException;
import soleng.framework.standard.common.ProjectPojo;
import soleng.framework.standard.protex.ProtexProjectPojo;
import soleng.framework.standard.protex.ProtexServerWrapper;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.license.LicenseAttributes;
import com.blackducksoftware.sdk.protex.license.LicenseExtensionLevel;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.bom.FileCountType;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeWithCount;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTreeWithCount;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.DiscoveryApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import com.blackducksoftware.soleng.bdsplugin.BDSPluginConstants;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginProtexConfigManager;
import com.blackducksoftware.soleng.bdsplugin.config.BDSPluginUser;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;
import com.blackducksoftware.soleng.bdsplugin.model.BDSProtexPojo;

public class ProtexDAO extends CommonDAO
{
    static Logger log = LoggerFactory.getLogger(ProtexDAO.class.getName());

    private ProjectApi projectApi = null;
    private CodeTreeApi codeTreeApi = null;
    private DiscoveryApi discoveryApi = null;
    private LicenseApi licenseApi = null;
    private IdentificationApi idApi = null;
    private BomApi bomApi = null;

    private Settings settings = null;

    private BDSPluginProtexConfigManager configManager = null;
    private ProtexServerWrapper protexWrapper = null;

    public ProtexDAO(Settings settings,
	    org.sonar.api.resources.Project sonarProject) throws Exception
    {
	this.settings = settings;
	authenticate();
    }

    @Override
    public void authenticate() throws Exception
    {
	try
	{
	    /**
	     * First we check to see if project settings contain data, if not
	     * use global.
	     */
	    Map<String, String> props = settings.getProperties();

	    String SERVER = settings
		    .getString(BDSPluginConstants.PROPERTY_PROTEX_URL);
	    String USER_NAME = settings
		    .getString(BDSPluginConstants.PROPERTY_PROTEX_USERNAME);
	    String PASSWORD = settings
		    .getString(BDSPluginConstants.PROPERTY_PROTEX_PASSWORD);
	    String PROJECT_NAME = settings
		    .getString(BDSPluginConstants.PROPERTY_PROTEX_PROJECT);


	    BDSPluginUser user = new BDSPluginUser(SERVER, USER_NAME, PASSWORD);

	    configManager = new BDSPluginProtexConfigManager(user);
	    configManager.setProtexPojectName(PROJECT_NAME);

	    configManager = (BDSPluginProtexConfigManager) collectGeneralSettings(
		    configManager, settings);

	    // workaround for this here
	    // http://fusesource.com/forums/thread.jspa?messageID=10988
	    Thread.currentThread().setContextClassLoader(
		    this.getClass().getClassLoader());
	    protexWrapper = new ProtexServerWrapper(
		    configManager.getServerBean(), configManager, false);

	    projectApi = protexWrapper.getInternalApiWrapper().projectApi;
	    codeTreeApi = protexWrapper.getInternalApiWrapper().codeTreeApi;
	    discoveryApi = protexWrapper.getInternalApiWrapper().discoveryApi;
	    licenseApi = protexWrapper.getInternalApiWrapper().licenseApi;
	    idApi = protexWrapper.getInternalApiWrapper().identificationApi;
	    bomApi = protexWrapper.getInternalApiWrapper().bomApi;

	    log.info("Protex authentication completed");
	} catch (Exception e)
	{
	    throw new Exception("Authentication failure: " + e.getMessage());
	}

    }

    public void populateProjectInfo(ApplicationPOJO pojo)
    {
	String pojoProjectName = null;
	try
	{
	    // Use the user specified project
	    pojoProjectName = configManager.getProtexPojectName();
	    if (pojoProjectName == null || pojoProjectName.length() == 0)
	    {
		// If it is empty, then take it from the Config Manager, Code
		// Center may have populated it.
		pojoProjectName = pojo.getProjectName();
	    }

	    // If it is still empty, then there is no sense in continuing.
	    if (pojoProjectName == null || pojoProjectName.length() == 0)
	    {
		throw new Exception("Nothing to analyze, project name is empty");
	    }

	    log.info("Getting project information for project name: "
		    + pojoProjectName);

	    // Get the project from the SDK, if the project has a bad name,
	    // missing, etc it will bomb.
	    // workaround for this here
	    // http://fusesource.com/forums/thread.jspa?messageID=10988
	    Thread.currentThread().setContextClassLoader(
		    this.getClass().getClassLoader());

	    ProtexProjectPojo projectPojo = (ProtexProjectPojo) protexWrapper
		    .getProjectByName(pojoProjectName);

	    pojo.setProjectID(projectPojo.getProjectKey());
	    pojo.setProjectName(projectPojo.getProjectName());
	    pojo.setDateLastAnalyzed(projectPojo.getAnalyzedDate());

	    // Grab the url
	    String server = settings
		    .getString(BDSPluginConstants.PROPERTY_PROTEX_URL);
	    // TODO: This is not working, just brings to main projectString
	    // protexBomURL = bomApi.getIdentifyBomUrl(project.getProjectId(),
	    // "/");
	    // TODO: Figure it out, this hard-coded stuff is terrible.
	    String protexBomURL = server + (server.endsWith("/") ? "" :  "/")
		    + "protex/ProtexIPIdentifyFolderBillOfMaterialsContainer?isAtTop=true&ProtexIPProjectId="
		    + projectPojo.getProjectKey()
		    + "&ProtexIPIdentifyFileViewLevel=folder&ProtexIPIdentifyFileId=-1";

	    pojo.setProtexBomURL(protexBomURL);

	    // Pack some information about Protex
	    BDSProtexPojo protexInfo = new BDSProtexPojo(projectPojo);
	    protexInfo.setProtexServer(server);
	    protexInfo.setProtexBomURL(protexBomURL);

	    pojo.setBDSProtexInfo(protexInfo);

	} catch (CommonFrameworkException cfe)
	{
	    log.error("Could not get project information for: "
		    + pojoProjectName);
	    log.error("Error: " + cfe.getMessage());
	    log.error("Configuration Information: "
		    + cfe.getConfigurationInformation());
	    pojo.setProtexErrorMsg(cfe.getMessage());
	} catch (Exception e)
	{
	    log.error("Error: " + e.getMessage());
	    pojo.setProtexErrorMsg(e.getMessage());
	}
	log.info("Got project information for: " + pojoProjectName);
    }

    public void populateProjectFileCounts(ApplicationPOJO pojo,
	    SensorContext sensorContext)
    {
	Integer totalFiles = new Integer(0);
	Integer totalPendingFiles = new Integer(0);
	Integer skippedFiles = new Integer(0);
	Integer noDiscoveryCount = new Integer(0);
	Integer discoveryCount = new Integer(0);
	Integer licenseConflictCount = new Integer(0);

	PartialCodeTree partialTree = null;

	try
	{
	    partialTree = // codeTreeApi.getCodeTree(pojo.getProjectID(), "/",
			  // CodeTreeUtilities.DIRECT_CHILDREN, true);
	    codeTreeApi.getCodeTree(pojo.getProjectID(), "/", 0, true);

	    PartialCodeTreeWithCount pctAnalyzedFileCount = codeTreeApi
		    .getAnalyzedFileCount(pojo.getProjectID(), partialTree);
	    CodeTreeNodeWithCount nodeCount = pctAnalyzedFileCount.getNodes()
		    .get(0);
	    totalFiles = nodeCount.getCount();

	    PartialCodeTreeWithCount skippedPctFileCount = codeTreeApi
		    .getSkippedFileCount(pojo.getProjectID(), partialTree);
	    nodeCount = skippedPctFileCount.getNodes().get(0);
	    skippedFiles = nodeCount.getCount();

	    PartialCodeTreeWithCount pctLicConflict = bomApi.getFileCount(
		    pojo.getProjectID(), partialTree,
		    FileCountType.LICENSE_CONFLICTS);
	    nodeCount = pctLicConflict.getNodes().get(0);
	    licenseConflictCount = nodeCount.getCount();

	} catch (Exception e)
	{
	    log.error("Error getting total project count: " + e.getMessage());
	}

	try
	{

	    PartialCodeTreeWithCount pctFileCount = discoveryApi
		    .getAllDiscoveriesPendingIdFileCount(pojo.getProjectID(),
			    partialTree);
	    CodeTreeNodeWithCount nodeCount = pctFileCount.getNodes().get(0);
	    totalPendingFiles = nodeCount.getCount();

	    PartialCodeTreeWithCount pctNoDiscoveryCount = discoveryApi
		    .getNoDiscoveriesFileCount(pojo.getProjectID(), partialTree);
	    CodeTreeNodeWithCount noDiscoveries = pctNoDiscoveryCount
		    .getNodes().get(0);
	    noDiscoveryCount = noDiscoveries.getCount();

	} catch (Exception e)
	{
	    log.error("Error getting pending file count: " + e.getMessage());
	}

	pojo.setTotalFileCount(totalFiles);
	pojo.setTotalPendingFileCount(totalPendingFiles);
	pojo.setTotalSkippedFileCount(skippedFiles);
	pojo.setTotalNoDiscoveryCount(noDiscoveryCount);
	pojo.setTotalLicenseConflictCount(licenseConflictCount);

	// This is the only value we calculate
	// But we must do it here and then store it as a metric to get Sonar
	// trending.
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
	    LicenseExtensionLevel licLevel = attributes
		    .getIntegrationLevelForLicenseApplication();

	    String licenseReachName = licLevel.toString();
	    if (licenseReachName == null)
		log.warn("Could not get license level reach name for license ID: "
			+ lic_id);
	    else
		licensePojo.setLicenseReachString(licenseReachName);

	    Integer ordinalNumber = licLevel.ordinal();
	    if (ordinalNumber == null)
		log.warn("Could not get ordinal value for license ID: "
			+ lic_id);
	    else
		licensePojo.setLicenseReachNumber(ordinalNumber);

	} catch (Exception e)
	{
	    log.error("Error: " + e.getMessage());
	}

	return licensePojo;
    }

}
