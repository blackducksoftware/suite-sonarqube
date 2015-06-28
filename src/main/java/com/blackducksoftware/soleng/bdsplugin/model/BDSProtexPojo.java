package com.blackducksoftware.soleng.bdsplugin.model;

import soleng.framework.standard.common.ProjectPojo;
import soleng.framework.standard.protex.ProtexProjectPojo;

/**
 * Little bean to hold Protex data that will be displayed in the 
 * Component Analaysis 'info' tooltip
 * 
 * Extends those methods which are not accessible via the common framework.
 * @author akamen
 *
 */

public class BDSProtexPojo extends ProtexProjectPojo {
	
	private String protexServer = null;
	private String protexBomURL = null;
	private ProtexProjectPojo protexProjectPojo = null;

	public BDSProtexPojo(ProtexProjectPojo projectPojo) 
	{
		setProtexProjectPojo(projectPojo);
	}
	public String getProtexServer() {
		return protexServer;
	}
	public void setProtexServer(String protexServer) {
		this.protexServer = protexServer;
	}

	public String getProtexBomURL() {
		return protexBomURL;
	}
	public void setProtexBomURL(String protexBomURL) {
		this.protexBomURL = protexBomURL;
	}
	public ProtexProjectPojo getProtexProjectPojo() {
		return protexProjectPojo;
	}
	private void setProtexProjectPojo(ProtexProjectPojo protexProjectPojo) {
		this.protexProjectPojo = protexProjectPojo;
	}
	
}
