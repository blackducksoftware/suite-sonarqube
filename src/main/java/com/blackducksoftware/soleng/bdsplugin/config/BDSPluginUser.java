package com.blackducksoftware.soleng.bdsplugin.config;

import soleng.framework.core.config.user.CommonUser;

public class BDSPluginUser implements CommonUser {

	private static final long serialVersionUID = 1L;
	private String server;
	private String user;
	private String password;

	
	public BDSPluginUser(String server, String user, String password) throws Exception
	{
		this.server = server;
		if(server == null || server.isEmpty())
		    throw new Exception("Server URL cannot be empty!");
		this.user = user;
		if(user == null || user.isEmpty())
		    throw new Exception("User name cannot be empty!");
		this.password = password;
		if(password == null || password.isEmpty())
		    throw new Exception("Password cannot be empty!");
	}
	
	@Override
	public void setServer(String servername) {
		this.server= servername;
		
	}

	@Override
	public void setUserName(String username) {
		this.user = username;
		
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
		
	}

	@Override
	public String getServer() {
		return this.server;
	}

	@Override
	public String getUserName() {
		return this.user;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

}
