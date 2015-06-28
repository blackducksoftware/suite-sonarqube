package com.blackducksoftware.soleng.bdsplugin;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import com.blackducksoftware.soleng.bdsplugin.dao.CodeCenterDAO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;


public class CodeCenterConnector 
{

	private static Logger log = LoggerFactory.getLogger(CodeCenterConnector.class.getName());
	
	private ApplicationPOJO pojo = null;
	
    private CodeCenterDAO ccDAO = null;	
	
    private Settings settings = null;

	public CodeCenterConnector(Settings settings, Project sonarProject) throws Exception
	{
		this.settings = settings;
		ccDAO = new CodeCenterDAO(settings, sonarProject);			
	}
	
	public ApplicationPOJO populateApplicationPojo(ApplicationPOJO pojo)
	{
		try{
			
			pojo = ccDAO.populateApplicationInfo(pojo);		
			pojo = ccDAO.populateComponentBreakdown(pojo);
			pojo = ccDAO.populateURLs(pojo, settings);
			// Optional depending on settings.
			pojo = ccDAO.collectCustomAttributes(pojo);
			
		} catch (Exception e)
		{
			log.error("Unable to populate Code Center data: " + e.getMessage());
			pojo.setCcErrorMsg(e.getMessage());
		}
		
		log.info("App information: " + pojo.toString());
		
		return pojo;
	}
	
}
