BD Suite plugin for SonarQube (Sonar)

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
		
		
! How to use it
# Configure global settings
	# Proxy
	# (Default) Protex server (+ credentials)
	# Code Center server (+ credentials)
# Run a project with the sonar runner (maven, command line ... does not matter)
# Once you have the project, go to settings and set
	# Protex project (*)
	# Code Center application/version
# Go to the project dashboard and add the widgets you like
	
	
(*) The project/code used for the sonarqube analysis and the code represented in Protex or Code Center, does not need to have any similarities or connections. All three are independent and the data is simply displayed in the dashboard.

!Release Notes
!!Version 2.0.0
* New version supporting the UI of SonarQube 5.1+
!!Version 1.2.2
* Fixed bug with password authentication failing
!!Version 1.2.0
* Initial release