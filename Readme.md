#BD Suite plugin for SonarQube (Sonar)

* Configures globally Proxy, default Protex server and CC server + login credentials and timeout values

* Configures per project
    * CC Application name and version
    * Px Projec tname
    * Px Protex server, if different from the default
* Adds four Widgets to the project dashboard
	* Component Analysis (Px)
		* files scanned, files with discoveries, files pending ID, ...
		* a percentage bar of how much ID work is still outstanding
		* a link to the Px Project
	* Component Approvals (CC)
		* Counts of components approved, pending, rejected, unknown
		* A table with components and their status and vulnerability counts
		* ?
		* link to CC application
	* Component Vulnerabilities (CC)
		* Counts of vulnerabilites for the entire Application
		* ...
	* Component License Information
		* License types
		* Conflicts
		
		
## How to use it?
* Configure global settings
	* Proxy
	* (Default) Protex server (+ credentials)
	* Code Center server (+ credentials)
* Run a project with the sonar runner (maven, command line ... does not matter)
	* use a local settings.xml
	
		<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
          <localRepository/>
          <interactiveMode/>
          <usePluginRegistry/>
          <offline/>
          <pluginGroups/>
          <servers/>
          <mirrors/>
          <proxies/>
    	  <profiles>
            <profile>
               <id>sonar</id>
               <activation>
                 <activeByDefault>true</activeByDefault>
               </activation>
               <properties>
                 <!-- Optional URL to server. Default value is http://localhost:9000 -->
                 <sonar.host.url>http://cc-integration.blackducksoftware.com:8090</sonar.host.url>
                 <sonar.jdbc.url>jdbc:postgresql://cc-integration.blackducksoftware.com:55430/sonar</sonar.jdbc.url>
                 <sonar.jdbc.username>sonar</sonar.jdbc.username>
                 <sonar.jdbc.password>sonar</sonar.jdbc.password>
               </properties>
             </profile>
          </profiles>
          <activeProfiles/>
		</settings>
		
  * Run
	
		env MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m" mvn -s ./settings.xml clean verify sonar:sonar
		
* Once you have the project, go to settings and set
	* Protex project (*)
	* Code Center application/version
	* Go to the project dashboard and add the widgets you like
	
	
(*) The project/code used for the sonarqube analysis and the code represented in Protex or Code Center, does not need to have any similarities or connections. All three are independent and the data is simply displayed in the dashboard.

#Release Notes
##Version 2.0.0
* New version supporting the UI of SonarQube 5.1+
##Version 1.2.2
* Fixed bug with password authentication failing
##Version 1.2.0
* Initial release