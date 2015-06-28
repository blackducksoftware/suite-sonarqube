/**
 * 
 */
package com.blackducksoftware.soleng.bdsplugin.model;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;

/**
 * Class that holds that basic component information
 * @author Ari
 *
 */
public class CompPOJO {

	private String componentName = null;
	private String version = null;
	private String id = null;
	private String requestType = null;
	private String url = null;
	private ApplicationIdToken appIdToken = null;  // This will only be non-null if the component is a rolled up App.
	private KbComponentIdToken kbCompId = null; // Temporary workaround until the appid gets fixed.
	
	// Counts for easy access
	private Integer vulnHigh = new Integer(0);
	private Integer vulnMed = new Integer(0);
	private Integer vulnLow = new Integer(0);
	
	public CompPOJO(String name, String version)
	{
		this.componentName = name;
		this.version = version;
	}
	
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		
		buff.append("\n");
		buff.append("Name: " + getComponentName());
		buff.append("\n");
		buff.append("Version: " + getVersion());
		buff.append("\n");
		buff.append("ID: " + getId());
		buff.append("\n");
		buff.append("Approval Status: " + getRequestType());
		buff.append("\n");
		buff.append("URL: " +url);
		buff.append("\n");
		buff.append("Vulnerability High: " + getVulnHigh());
		buff.append("\n");
		buff.append("Vulnerability Med: " + getVulnMed());
		buff.append("\n");
		buff.append("Vulnerability Low: " + getVulnLow());
		buff.append("\n");
		return buff.toString();
		
	}


	public Integer getVulnHigh() {
		return vulnHigh;
	}
	public Integer getVulnMed() {
		return vulnMed;
	}

	public Integer getVulnLow() {
		return vulnLow;
	}

	public void addVulnLow() {
		this.vulnLow = vulnLow + 1;
	}
	
	public void addVulnHigh() {
		this.vulnHigh = vulnHigh + 1;
	}
	
	public void addVulnMed() {
		this.vulnMed = vulnMed + 1;
	}

	public ApplicationIdToken getAppIdToken() {
		return appIdToken;
	}

	public void setAppIdToken(ApplicationIdToken appIdToken) {
		this.appIdToken = appIdToken;
	}

	public KbComponentIdToken getKbCompId() {
		return kbCompId;
	}

	public void setKbCompId(KbComponentIdToken kbCompId) {
		this.kbCompId = kbCompId;
	}


}
