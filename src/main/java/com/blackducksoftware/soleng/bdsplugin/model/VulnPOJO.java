package com.blackducksoftware.soleng.bdsplugin.model;

import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO.VULNERABILITY_SEVERITY;

/**
 * Basic bean that contains vulnerability information
 * @author akamen
 *
 */
public class VulnPOJO {

	private String name;
	private String publishDate;
	private String comments;
	private String description;
	private String componentName;
	private String componentVersion;
	
	private VULNERABILITY_SEVERITY severity;
	
	public VulnPOJO(String name)
	{
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public VULNERABILITY_SEVERITY getSeverity() {
		return severity;
	}
	public void setSeverity(VULNERABILITY_SEVERITY severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + this.name);
		sb.append("\n");
		sb.append("Belongs to component: " + this.componentName);
		sb.append("\n");
		sb.append("Severity: " + this.severity);
		sb.append("\n");
		
		return sb.toString();
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public void setComponentVersion(String componentVersion) {
		this.componentVersion = componentVersion;
	}
	
	
}
