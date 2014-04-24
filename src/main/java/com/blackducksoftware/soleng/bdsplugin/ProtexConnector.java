package com.blackducksoftware.soleng.bdsplugin;

import java.util.HashMap;




import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;

import com.blackducksoftware.soleng.bdsplugin.dao.ProtexDAO;
import com.blackducksoftware.soleng.bdsplugin.model.ApplicationPOJO;
import com.blackducksoftware.soleng.bdsplugin.model.LicenseCategory;
import com.blackducksoftware.soleng.bdsplugin.model.LicensePOJO;



/**
 * 
 * Simple class to connect to Protex using the SDK
 * Should use the DAO
 * 
 * @author Ari
 *
 */
public class ProtexConnector {

	private static final Logger log = LoggerFactory.getLogger(ProtexConnector.class);
	
	
	private ProtexDAO pDAO = null;
	

	public ProtexConnector(Settings settings) throws Exception
	{
		pDAO = new ProtexDAO(settings);			
	}
	
	public ApplicationPOJO populateApplicationWithProtexData(ApplicationPOJO pojo, SensorContext sensorContext) 
	{
		try{
			pDAO.populateProjectInfo(pojo);
			
			pDAO.populateProjectFileCounts(pojo, sensorContext);
					
			log.info("App information: " + pojo.toString());
		} 
		catch (Exception e)
		{
			log.error("Unable to populate Protex data", e);
			pojo.setProtexErrorMsg(e.getMessage());
		}
		catch (Throwable t)
		{
			pojo.setProtexErrorMsg(t.getMessage());
			log.error("Fatal Protex error: Unable to generate Protex information", t);
		}
		
		return pojo;
	}

	/**
	 * Uses the DAO to populate the license pojo with:
	 * license reach level, license attribute name
	 * 
	 * Then sorts the license objects based on name/frequency/number
	 * @param pojo
	 * @return
	 */
	public ApplicationPOJO populateLicenseData(ApplicationPOJO pojo) 
	{		
		for(LicensePOJO license  : pojo.getLicenses())
		{
			pDAO.populateLicenseAttributes(license);		
		}

		sortLicenses(pojo);
		
		return pojo;
	}

	/**
	 * Iterates through all the license pojos and determines their frequency and how should they be sorted.
	 * 
	 * TODO:  Extract these rules into a config (even though it will seldom change0
	 * TODO:  We have to use the unknown name instead of relying on number, due to the limitation of Protex SDK (New SDK fixes promised to be delivered soon)
	 * TODO:  This will change in 7.0 with all these categories being  baked in.
	 * Name Unknown:  Unknown
	 * Number < 2:  Permissive
	 * Number >=2 && < 5:   Weak Reciprocal
	 * Number >= 5:    Reciprocal
	 * @param pojo
	 */
	private void sortLicenses(ApplicationPOJO pojo) 
	{
		HashMap<String, LicenseCategory> sortedLicenseMap = new HashMap<String, LicenseCategory>();
		List<LicensePOJO> licenses = pojo.getLicenses();
		
		for(LicensePOJO license : licenses)
		{
			Integer reachNumber = license.getLicenseReachNumber();
			if(license.getLicenseID().equals("unknown"))
			{
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_UNKNOWN,"gray");
			}
			else if(reachNumber < 2)
			{
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_PERMISSIVE,"green");
			}
			else if(reachNumber >= 2 && reachNumber < 5)
			{
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_WEAK_RECIPROCAL,"orange");
			}
			else if(reachNumber >= 5)
			{
				populateMap(sortedLicenseMap, BDSPluginConstants.LICENSE_CATEGORY_RECIPROCAL,"red");
			}
		}
		
		pojo.setSortedLicenseMap(sortedLicenseMap);
	}
	
	
	
	private void populateMap(HashMap<String, LicenseCategory> sortedLicenseMap, String key, String color)
	{
		LicenseCategory category = sortedLicenseMap.get(key);
		if(category == null)
		{
			LicenseCategory cat = new LicenseCategory(key, color);
			cat.incrementCount();
			sortedLicenseMap.put(key, cat);
			log.debug("New license category entry for: " + key);
		}			
		else
		{
			category.incrementCount();
			log.debug("Adding existing license category for key: " + key + " count: " + category.getCount());
		}
	}
	
}
