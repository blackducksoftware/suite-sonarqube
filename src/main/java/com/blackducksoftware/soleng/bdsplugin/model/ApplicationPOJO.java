package com.blackducksoftware.soleng.bdsplugin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Holds all the information for our project/application regardless of which backend the data comes from
 * @author Ari
 *
 */
public class ApplicationPOJO 
{
	// Code Center
	private String appName = "";
	private String appVersion = "";
	private String appDescription = "";

	/**
	 * Component Information
	 */
	// The following are derived by the DAO
	// Components from approved requests
	private List<CompPOJO>  componentsApproved = new ArrayList<CompPOJO>();
	// Components from rejected requests
	private List<CompPOJO>  componentsRejected = new ArrayList<CompPOJO>();
	// Components from pending requests
	private List<CompPOJO>  componentsPending = new ArrayList<CompPOJO>();
	// Components from default/unknown requests
	private List<CompPOJO>  componentsUnknown = new ArrayList<CompPOJO>();
	// The following are just size lists from the above lists
	private Integer approvedComponentCount = new Integer(0);
	private Integer rejectedComponentCount = new Integer(0);
	private Integer pendingComponentCount = new Integer(0);
	private Integer unknownomponentCount = new Integer(0);
	/**
	 * Vulnerability Information
	 */
	private List<VulnPOJO>  vulnHighList = new ArrayList<VulnPOJO>();
	private List<VulnPOJO>  vulnMedList = new ArrayList<VulnPOJO>();
	private List<VulnPOJO>  vulnLowList = new ArrayList<VulnPOJO>();
	
	private Integer vulnHighCount = new Integer(0);
	private Integer vulnMedCount = new Integer(0);
	private Integer vulnLowCount = new Integer(0);
	
	private String codeCenterBomPage = "";
	
	// Raw list of IDs
	private List<LicensePOJO> licenses = new ArrayList<LicensePOJO>();
	
	// Sorted license map based on attributed
	private HashMap<String, LicenseCategory> sortedLicenseMap = new HashMap<String, LicenseCategory>();
	
	/// Protex Information
	private String projectID = ""; 	//  Protex Project ID
	private String projectName = "";
	private Integer totalFileCount = 0;
	private Integer totalPendingFileCount = 0;
	private Integer totalSkippedFileCount = 0;
	private Integer totalNoDiscoveryCount = 0;
	private Integer totalDiscoveryCount = 0;
	private Integer totalLicenseConflictCount = 0;
	private String dateLastAnalyzed = "";
	// This is the last time Protex had a BOM refresh.  
	// We track this so that we only update our internal counts when the date is newer.
	private Date lastrefreshDate = null;
	
	// Map of components based on their request
	private HashMap<String, String> componentRequestMap = new HashMap<String, String>();
	
	// Map of components and their URLs
	private HashMap<String, String> componentURLMap = new HashMap<String, String>();
		
	
	public static enum VULNERABILITY_SEVERITY {HIGH, MEDIUM, LOW};
	
	public ApplicationPOJO()
	{
		
	}

	
	public String toString()
	{
		StringBuilder buff = new StringBuilder();
		
		try{
			buff.append("\n");
			buff.append("Name: " + getAppName());
			buff.append("\n");
			buff.append("Version: " + getAppVersion());
			buff.append("\n");
			buff.append("Description: " + getAppDescription());
			buff.append("\n");
			buff.append("Protex Project ID: " + this.getProjectID());
			buff.append("\n");
			buff.append("Protex Project Name: " + this.getProjectName());
			buff.append("\n");
			buff.append("Protex total analyzed files: " + this.getTotalFileCount());
			buff.append("\n");
			buff.append("Protex total pending ID files: " + this.getTotalPendingFileCount());
			buff.append("\n");
			buff.append("Protex total skipped files: " + this.getTotalSkippedFileCount());
			buff.append("\n");
			buff.append("Protex total no discovery files: " + this.getTotalNoDiscoveryCount());
			buff.append("\n");
			buff.append("Protex total discovery files: " + this.getTotalDiscoveryCount());
			buff.append("\n");
			buff.append("Protex total license conflict files: " + this.getTotalLicenseConflictCount());
			buff.append("\n");
			buff.append("Protex last analyzed date: " + this.getDateLastAnalyzed());
			buff.append("\n");
			buff.append("Protex last BOM Refresh date: " + this.lastrefreshDate);
			buff.append("\n");
			buff.append("Approved: " + this.approvedComponentCount);
			buff.append("\n");
			buff.append("Pending: " + this.pendingComponentCount);
			buff.append("\n");
			buff.append("Rejected: " + this.rejectedComponentCount);
			buff.append("\n");
			buff.append("Low Vulnerability: " + this.getVulnHighCount());
			buff.append("\n");
			buff.append("Medium Vulnerability: " + this.getVulnMedCount());
			buff.append("\n");
			buff.append("High Vulnerability: " + this.getVulnLowCount());
			buff.append("\n");
			buff.append("Sorted License Map: " + this.getSortedLicenseMap());
		} catch (Exception e)
		{
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

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}


	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public Integer getTotalFileCount() {
		return totalFileCount;
	}

	public void setTotalFileCount(Integer totalFileCount) {
		this.totalFileCount = totalFileCount;
	}

	public Integer getTotalPendingFileCount() {
		return totalPendingFileCount;
	}

	public void setTotalPendingFileCount(Integer totalPendingFileCount) {
		this.totalPendingFileCount = totalPendingFileCount;
	}
	
	/**
	 * Adds a license pojo object to the internal list
	 * @param licPojo
	 */
	public void addLicense(LicensePOJO licPojo)
	{
		licenses.add(licPojo);
	}
	
	public List<LicensePOJO> getLicenses()
	{
		return licenses;
	}

	public HashMap<String, LicenseCategory> getSortedLicenseMap() {
		return sortedLicenseMap;
	}

	public void setSortedLicenseMap(HashMap<String, LicenseCategory> sortedLicenseMap) {
		this.sortedLicenseMap = sortedLicenseMap;
	}


	public List<CompPOJO>  getComponentsApproved() {
		return componentsApproved;
	}


	public void setComponentsApproved(List<CompPOJO>  componentsApproved) 
	{
		this.componentsApproved = componentsApproved;
		this.approvedComponentCount = componentsApproved.size();
	}


	public List<CompPOJO>  getComponentsRejected() {
		return componentsRejected;
	}


	public void setComponentsRejected(List<CompPOJO>  componentsRejected) {
		this.componentsRejected = componentsRejected;
		this.rejectedComponentCount = componentsRejected.size();
	}


	public List<CompPOJO>  getComponentsPending() {
		return componentsPending;
	}


	public void setComponentsPending(List<CompPOJO>  componentsPending) {
		this.componentsPending = componentsPending;
		this.pendingComponentCount = componentsPending.size();
	}

	public void setComponentsUnknown(List<CompPOJO> componentsUnknown) {
		this.componentsUnknown = componentsUnknown;
		this.unknownomponentCount = componentsUnknown.size();
	}
	
	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public HashMap<String, String> getComponentRequestMap() {
		return componentRequestMap;
	}

	/**
	 * Adds name of component based on request
	 * @param key Request type
	 * @param value Name of Component
	 */
	public void addComponentToRequestMap(String key, String value) 
	{
		String existingValue = componentRequestMap.get(key);
		if(existingValue == null)
			componentRequestMap.put(key, value);
		else
		{
			existingValue = existingValue.concat(",").concat(value);
			componentRequestMap.put(key, existingValue);	
		}
	}


	public HashMap<String, String> getComponentURLMap() {
		return componentURLMap;
	}

	public void addComponentToURLMap(String key, String value) 
	{	
		componentURLMap.put(key, value);
	}


	public Integer getTotalNoDiscoveryCount() {
		return totalNoDiscoveryCount;
	}


	public void setTotalNoDiscoveryCount(Integer totalNoDiscoveryCount) {
		this.totalNoDiscoveryCount = totalNoDiscoveryCount;
	}


	public Integer getTotalSkippedFileCount() {
		return totalSkippedFileCount;
	}


	public void setTotalSkippedFileCount(Integer totalSkippedFileCount) {
		this.totalSkippedFileCount = totalSkippedFileCount;
	}


	public Integer getTotalDiscoveryCount() {
		return totalDiscoveryCount;
	}


	public void setTotalDiscoveryCount(Integer totalDiscoveryCount) {
		this.totalDiscoveryCount = totalDiscoveryCount;
	}


	public String getDateLastAnalyzed() {
		return dateLastAnalyzed;
	}


	public void setDateLastAnalyzed(String dateLastAnalyzed) {
		this.dateLastAnalyzed = dateLastAnalyzed;
	}


	public Date getLastrefreshDate() {
		return lastrefreshDate;
	}


	public void setLastrefreshDate(Date lastrefreshDate) {
		this.lastrefreshDate = lastrefreshDate;
	}


	public Integer getTotalLicenseConflictCount() {
		return totalLicenseConflictCount;
	}


	public void setTotalLicenseConflictCount(Integer totalLicenseConflictCount) {
		this.totalLicenseConflictCount = totalLicenseConflictCount;
	}


	public String getCodeCenterBomPage() {
		return codeCenterBomPage;
	}


	public void setCodeCenterBomPage(String codeCenterBomPage) {
		this.codeCenterBomPage = codeCenterBomPage;
	}

	public Integer getApprovedComponentCount()
	{
		return this.approvedComponentCount;
	}
	
	public Integer getRejectedComponentCount()
	{
		return this.rejectedComponentCount;
	}
	
	public Integer getPendingComponentCount()
	{
		return this.pendingComponentCount;
	}

	public Integer getUnknownComponentCount()
	{
		return this.unknownomponentCount;
	}


	public List<CompPOJO> getComponentsUnknown() {
		return componentsUnknown;
	}


	public List<VulnPOJO> getVulnHighList() {
		return vulnHighList;
	}


	public void addVulnHighList(VulnPOJO vulnHigh) {
		vulnHighList.add(vulnHigh);
		this.vulnHighCount = vulnHighList.size();
	}


	public List<VulnPOJO> getVulnMedList() {
		return vulnMedList;
	}


	public void addVulnMedList(VulnPOJO vulnMed) {
		vulnMedList.add(vulnMed);
		this.vulnMedCount = vulnMedList.size();
	}


	public List<VulnPOJO> getVulnLowList() {
		return vulnLowList;
	}


	public void addVulnLowList(VulnPOJO vulnLow) {
		vulnLowList.add(vulnLow);
		this.vulnLowCount = vulnLowList.size();
	}





}
