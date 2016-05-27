package com.blackducksoftware.soleng.bdsplugin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;

/**
 * Holds all the information for our project/application regardless of which
 * backend the data comes from
 * 
 * @author Ari
 * 
 */
public class ApplicationPOJO {
	// Code Center
	private ApplicationIdToken applicationId = null; // This is the CC ID for
														// your application.

	private String appName = "";

	private String appVersion = "";

	private String appDescription = "";

	/**
	 * Error Messages
	 */
	private String protexErrorMsg;

	private String ccErrorMsg;

	/**
	 * Component Information
	 */
	// The following are derived by the DAO
	// Components from approved requests
	private List<CompPOJO> componentsApproved = new ArrayList<CompPOJO>();

	// Components from rejected requests
	private List<CompPOJO> componentsRejected = new ArrayList<CompPOJO>();

	// Components from pending requests
	private List<CompPOJO> componentsPending = new ArrayList<CompPOJO>();

	// Components from default/unknown requests
	private List<CompPOJO> componentsUnknown = new ArrayList<CompPOJO>();

	// The following are just size lists from the above lists
	private Integer approvedComponentCount = new Integer(0);

	private Integer rejectedComponentCount = new Integer(0);

	private Integer pendingComponentCount = new Integer(0);

	private Integer unknownomponentCount = new Integer(0);

	/**
	 * Vulnerability Information
	 */
	private List<VulnPOJO> vulnHighList = new ArrayList<VulnPOJO>();

	private List<VulnPOJO> vulnMedList = new ArrayList<VulnPOJO>();

	private List<VulnPOJO> vulnLowList = new ArrayList<VulnPOJO>();

	private Integer vulnHighCount = new Integer(0);

	private Integer vulnMedCount = new Integer(0);

	private Integer vulnLowCount = new Integer(0);

	/**
	 * Optional attribute list
	 */
	private List<AttributePOJO> attributes = new ArrayList<AttributePOJO>();

	private String codeCenterBomPage = "";

	// Raw list of IDs
	private List<LicensePOJO> licenses = new ArrayList<LicensePOJO>();

	// Sorted license map based on attributed
	private HashMap<String, LicenseCategory> sortedLicenseMap = new HashMap<String, LicenseCategory>();

	// / Protex Information
	private String projectID = ""; // Protex Project ID

	private String projectName = "";

	private Long totalFileCount = new Long(0);
	private Long totalPendingFileCount = new Long(0);
	private Long totalSkippedFileCount = new Long(0);
	private Long totalNoDiscoveryCount = new Long(0);
	private Long totalDiscoveryCount = new Long(0);
	private Long totalLicenseConflictCount = new Long(0);

	private String dateLastAnalyzed = "";

	// This is the last time Protex had a BOM refresh.
	// We track this so that we only update our internal counts when the date is
	// newer.
	private Date lastrefreshDate = null;

	private String protexBomURL = null;

	private BDSProtexPojo protexProject = null;

	// Map of components based on their request
	private HashMap<String, String> componentRequestMap = new HashMap<String, String>();

	// Map of components and their URLs
	private HashMap<String, String> componentURLMap = new HashMap<String, String>();

	public static enum VULNERABILITY_SEVERITY {
		HIGH, MEDIUM, LOW
	};

	public ApplicationPOJO() {

	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();

		try {
			buff.append("\n");
			buff.append("Name: " + getAppName());
			buff.append("\n");
			buff.append("Version: " + getAppVersion());
			buff.append("\n");
			buff.append("Description: " + getAppDescription());
			buff.append("\n");
			buff.append("Protex Project ID: " + getProjectID());
			buff.append("\n");
			buff.append("Protex Project Name: " + getProjectName());
			buff.append("\n");
			buff.append("Protex total analyzed files: " + getTotalFileCount());
			buff.append("\n");
			buff.append("Protex total pending ID files: " + getTotalPendingFileCount());
			buff.append("\n");
			buff.append("Protex total skipped files: " + getTotalSkippedFileCount());
			buff.append("\n");
			buff.append("Protex total no discovery files: " + getTotalNoDiscoveryCount());
			buff.append("\n");
			buff.append("Protex total discovery files: " + getTotalDiscoveryCount());
			buff.append("\n");
			buff.append("Protex total license conflict files: " + getTotalLicenseConflictCount());
			buff.append("\n");
			buff.append("Protex last analyzed date: " + getDateLastAnalyzed());
			buff.append("\n");
			buff.append("Protex last BOM Refresh date: " + lastrefreshDate);
			buff.append("\n");
			buff.append("Approved: " + approvedComponentCount);
			buff.append("\n");
			buff.append("Pending: " + pendingComponentCount);
			buff.append("\n");
			buff.append("Rejected: " + rejectedComponentCount);
			buff.append("\n");
			buff.append("Low Vulnerability: " + getVulnHighCount());
			buff.append("\n");
			buff.append("Medium Vulnerability: " + getVulnMedCount());
			buff.append("\n");
			buff.append("High Vulnerability: " + getVulnLowCount());
			buff.append("\n");
			buff.append("Sorted License Map: " + getSortedLicenseMap());
			buff.append("\n");
			buff.append("Attributes: " + getAttributes().size());
			buff.append("\n");
			buff.append("Protex error: " + getProtexErrorMsg());
			buff.append("\n");
			buff.append("CC error: " + getCcErrorMsg());
		} catch (Exception e) {
			// ignore
		}
		return buff.toString();

	}

	public Integer getVulnHighCount() {
		return vulnHighCount;
	}

	public Integer getVulnMedCount() {
		return vulnMedCount;
	}

	public Integer getVulnLowCount() {
		return vulnLowCount;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(final String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(final String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppDescription(final String appDescription) {
		this.appDescription = appDescription;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(final String projectID) {
		this.projectID = projectID;
	}

	public Long getTotalFileCount() {
		return totalFileCount;
	}

	public void setTotalFileCount(final Long totalFileCount) {
		this.totalFileCount = totalFileCount;
	}

	public Long getTotalPendingFileCount() {
		return totalPendingFileCount;
	}

	public void setTotalPendingFileCount(final Long totalPendingFileCount) {
		this.totalPendingFileCount = totalPendingFileCount;
	}

	/**
	 * Adds a license pojo object to the internal list
	 * 
	 * @param licPojo
	 */
	public void addLicense(final LicensePOJO licPojo) {
		licenses.add(licPojo);
	}

	public List<LicensePOJO> getLicenses() {
		return licenses;
	}

	public HashMap<String, LicenseCategory> getSortedLicenseMap() {
		return sortedLicenseMap;
	}

	public void setSortedLicenseMap(final HashMap<String, LicenseCategory> sortedLicenseMap) {
		this.sortedLicenseMap = sortedLicenseMap;
	}

	public List<CompPOJO> getComponentsApproved() {
		return componentsApproved;
	}

	public void setComponentsApproved(final List<CompPOJO> componentsApproved) {
		this.componentsApproved = componentsApproved;
		approvedComponentCount = componentsApproved.size();
	}

	public List<CompPOJO> getComponentsRejected() {
		return componentsRejected;
	}

	public void setComponentsRejected(final List<CompPOJO> componentsRejected) {
		this.componentsRejected = componentsRejected;
		rejectedComponentCount = componentsRejected.size();
	}

	public List<CompPOJO> getComponentsPending() {
		return componentsPending;
	}

	public void setComponentsPending(final List<CompPOJO> componentsPending) {
		this.componentsPending = componentsPending;
		pendingComponentCount = componentsPending.size();
	}

	public void setComponentsUnknown(final List<CompPOJO> componentsUnknown) {
		this.componentsUnknown = componentsUnknown;
		unknownomponentCount = componentsUnknown.size();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	public HashMap<String, String> getComponentRequestMap() {
		return componentRequestMap;
	}

	/**
	 * Adds name of component based on request
	 * 
	 * @param key
	 *            Request type
	 * @param value
	 *            Name of Component
	 */
	public void addComponentToRequestMap(final String key, final String value) {
		String existingValue = componentRequestMap.get(key);
		if (existingValue == null) {
			componentRequestMap.put(key, value);
		} else {
			existingValue = existingValue.concat(",").concat(value);
			componentRequestMap.put(key, existingValue);
		}
	}

	public HashMap<String, String> getComponentURLMap() {
		return componentURLMap;
	}

	public void addComponentToURLMap(final String key, final String value) {
		componentURLMap.put(key, value);
	}

	public Long getTotalNoDiscoveryCount() {
		return totalNoDiscoveryCount;
	}

	public void setTotalNoDiscoveryCount(final Long totalNoDiscoveryCount) {
		this.totalNoDiscoveryCount = totalNoDiscoveryCount;
	}

	public Long getTotalSkippedFileCount() {
		return totalSkippedFileCount;
	}

	public void setTotalSkippedFileCount(final Long totalSkippedFileCount) {
		this.totalSkippedFileCount = totalSkippedFileCount;
	}

	public Long getTotalDiscoveryCount() {
		return totalDiscoveryCount;
	}

	public void setTotalDiscoveryCount(final Long totalDiscoveryCount) {
		this.totalDiscoveryCount = totalDiscoveryCount;
	}

	public String getDateLastAnalyzed() {
		return dateLastAnalyzed;
	}

	public void setDateLastAnalyzed(final String dateLastAnalyzed) {
		this.dateLastAnalyzed = dateLastAnalyzed;
	}

	public Date getLastrefreshDate() {
		return lastrefreshDate;
	}

	public void setLastrefreshDate(final Date lastrefreshDate) {
		this.lastrefreshDate = lastrefreshDate;
	}

	public Long getTotalLicenseConflictCount() {
		return totalLicenseConflictCount;
	}

	public void setTotalLicenseConflictCount(final Long totalLicenseConflictCount) {
		this.totalLicenseConflictCount = totalLicenseConflictCount;
	}

	public String getCodeCenterBomPage() {
		return codeCenterBomPage;
	}

	public void setCodeCenterBomPage(final String codeCenterBomPage) {
		this.codeCenterBomPage = codeCenterBomPage;
	}

	public Integer getApprovedComponentCount() {
		return approvedComponentCount;
	}

	public Integer getRejectedComponentCount() {
		return rejectedComponentCount;
	}

	public Integer getPendingComponentCount() {
		return pendingComponentCount;
	}

	public Integer getUnknownComponentCount() {
		return unknownomponentCount;
	}

	public List<CompPOJO> getComponentsUnknown() {
		return componentsUnknown;
	}

	public List<VulnPOJO> getVulnHighList() {
		return vulnHighList;
	}

	public void addVulnHighList(final VulnPOJO vulnHigh) {
		vulnHighList.add(vulnHigh);
		vulnHighCount = vulnHighList.size();
	}

	public List<VulnPOJO> getVulnMedList() {
		return vulnMedList;
	}

	public void addVulnMedList(final VulnPOJO vulnMed) {
		vulnMedList.add(vulnMed);
		vulnMedCount = vulnMedList.size();
	}

	public List<VulnPOJO> getVulnLowList() {
		return vulnLowList;
	}

	public void addVulnLowList(final VulnPOJO vulnLow) {
		vulnLowList.add(vulnLow);
		vulnLowCount = vulnLowList.size();
	}

	public String getProtexErrorMsg() {
		return protexErrorMsg;
	}

	public void setProtexErrorMsg(final String protexErrorMsg) {
		this.protexErrorMsg = protexErrorMsg;
	}

	public String getCcErrorMsg() {
		if (ccErrorMsg == null) {
			ccErrorMsg = "";
		}
		return ccErrorMsg;
	}

	public void setCcErrorMsg(final String ccErrorMsg) {
		this.ccErrorMsg = ccErrorMsg;
	}

	public ApplicationIdToken getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(final ApplicationIdToken applicationId) {
		this.applicationId = applicationId;
	}

	public List<AttributePOJO> getAttributes() {
		return attributes;
	}

	public void addAttributes(final AttributePOJO attribute) {
		attributes.add(attribute);
	}

	public String getProtexBomURL() {
		return protexBomURL;
	}

	public void setProtexBomURL(final String protexBomURL) {
		this.protexBomURL = protexBomURL;
	}

	public BDSProtexPojo getProtexProject() {
		return protexProject;
	}

	public void setProtexProject(final BDSProtexPojo protexInfo) {
		this.protexProject = protexInfo;
	}

}
