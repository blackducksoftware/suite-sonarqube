package com.blackducksoftware.soleng.bdsplugin;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;

import com.blackducksoftware.soleng.bdsplugin.dao.CodeCenterDAO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;


public class CodeCenterConnector 
{

	private static Logger log = LoggerFactory.getLogger(CodeCenterConnector.class.getName());
	
	private ApplicationPOJO pojo = null;
	
    private CodeCenterDAO ccDAO = null;	
	
    private Settings settings = null;
    
	public CodeCenterConnector(Settings settings, String sonarProjectName)
	{
		this.settings = settings;
		ccDAO = new CodeCenterDAO(settings, sonarProjectName);	
		
	}
	
	public ApplicationPOJO populateApplicationPojo(ApplicationPOJO pojo)
	{
		try{
			
			pojo = ccDAO.populateApplicationInfo(pojo);		
			pojo = ccDAO.populateComponentBreakdown(pojo);
			pojo = ccDAO.populateURLs(pojo, settings);
		} catch (Exception e)
		{
			log.error("Unable to populate Code Center data: " + e.getMessage());
		}
		
		log.info("App information: " + pojo.toString());
		
		return pojo;
	}
	
}
