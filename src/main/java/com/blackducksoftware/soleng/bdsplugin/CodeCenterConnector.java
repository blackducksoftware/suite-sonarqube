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

    private CodeCenterDAO ccDAO = null;

    private Settings settings = null;

    public CodeCenterConnector(Settings settings, Project sonarProject) throws Exception
    {
        this.settings = settings;
        ccDAO = new CodeCenterDAO(settings, sonarProject);
    }

    public ApplicationPOJO populateApplicationPojo(ApplicationPOJO pojo)
    {
        try {
            pojo = ccDAO.populateApplicationInfo(pojo);
            // FIXME CC-13128
            /*
             * Hack!!!
             * Dangerous workaround for CC-13128
             * Don't change the order of the following statements
             */
            pojo = ccDAO.populateComponentBreakdown(pojo);
            pojo = ccDAO.populateURLs(pojo, settings);
            /*
             * End of Hack
             */
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
