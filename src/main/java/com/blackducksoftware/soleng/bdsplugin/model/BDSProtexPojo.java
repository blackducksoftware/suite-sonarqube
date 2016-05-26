package com.blackducksoftware.soleng.bdsplugin.model;

import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;

/**
 * Little bean to hold Protex data that will be displayed in the Component
 * Analaysis 'info' tooltip
 * 
 * Extends those methods which are not accessible via the common framework.
 * 
 * @author akamen
 * 
 */

public class BDSProtexPojo extends ProtexProjectPojo {

	private String protexServer = null;

	private String protexBomURL = null;

	private ProtexProjectPojo protexProjectPojo = null;

	public BDSProtexPojo(final ProtexProjectPojo projectPojo) {
		setProtexProjectPojo(projectPojo);
	}

	public String getProtexServer() {
		return protexServer;
	}

	public void setProtexServer(final String protexServer) {
		this.protexServer = protexServer;
	}

	public String getProtexBomURL() {
		return protexBomURL;
	}

	public void setProtexBomURL(final String protexBomURL) {
		this.protexBomURL = protexBomURL;
	}

	public ProtexProjectPojo getProtexProjectPojo() {
		return protexProjectPojo;
	}

	private void setProtexProjectPojo(final ProtexProjectPojo protexProjectPojo) {
		this.protexProjectPojo = protexProjectPojo;
		super.setProjectKey(protexProjectPojo.getProjectKey());
		super.setProjectName(protexProjectPojo.getProjectName());
	}

}
