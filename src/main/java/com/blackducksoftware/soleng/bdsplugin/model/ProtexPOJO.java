package com.blackducksoftware.soleng.bdsplugin.model;

/**
 * Little bean to hold Protex data that will be displayed in the 
 * Component Analaysis 'info' tooltip
 * @author akamen
 *
 */

public class ProtexPOJO {
	
	private String protexAnalyzedDate = null;
	private String protexServer = null;
	private String protexProjectName = null;
	private String protexBomURL = null;
	
	public String getProtexProjectName() {
		return protexProjectName;
	}
	public void setProtexProjectName(String protexProjectName) {
		this.protexProjectName = protexProjectName;
	}
	public String getProtexServer() {
		return protexServer;
	}
	public void setProtexServer(String protexServer) {
		this.protexServer = protexServer;
	}
	public String getProtexAnalyzedDate() {
		return protexAnalyzedDate;
	}
	public void setProtexAnalyzedDate(String protexAnalyzedDate) {
		this.protexAnalyzedDate = protexAnalyzedDate;
	}
	public String getProtexBomURL() {
		return protexBomURL;
	}
	public void setProtexBomURL(String protexBomURL) {
		this.protexBomURL = protexBomURL;
	}
	
}
