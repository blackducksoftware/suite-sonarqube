package com.blackducksoftware.soleng.bdsplugin;

public class BDSPluginConstants {

	/**
	 * Settings
	 */
	// General property keys
	public static final String PROPERTY_PROXY_SERVER = "ProxyServerKey";
	public static final String PROPERTY_PROXY_PORT = "ProxyPortKey";
	public static final String PROPERTY_PROXY_PROTOCOL = "ProxyProtocolKey";
	public static final String PROXY_PROTOCOL_HTTP = "HTTP"; // Used an option in the pulldown
	public static final String PROXY_PROTOCOL_SSL = "SSL"; // Used an option in the pulldown
	
	// Property Keys
	public static final String PROPERTY_PROTEX_PROJECT = "ProtexProjectKey";
	public static final String PROPERTY_PROTEX_URL = "ProtexUrlKey";
	public static final String PROPERTY_PROTEX_USERNAME = "ProtexUserKey";
	public static final String PROPERTY_PROTEX_PASSWORD = "ProtexPassword";
	public static final String PROPERTY_PROTEX_REFRESH_BOM_DATE = "ProtexBomRefreshDate";
	
	
	public static final String PROPERTY_CC_PROJECT = "CCProjectKey";
	public static final String PROPERTY_CC_VERSION = "CCVersionKey";
	public static final String PROPERTY_CC_URL = "CCUrlKey";
	public static final String PROPERTY_CC_USERNAME = "CCUserNameKey";
	public static final String PROPERTY_CC_PASSSWORD = "CCPasswordKey";
	public static final String PROPERTY_PROTEX_LICENSE_COLORS = "ProtexLicenseColors";


	
	/**
	 * Metrics/Measures
	 */
	// License category constants for license breakdown map
	public static String LICENSE_CATEGORY_UNKNOWN = "Unknown";
	public static String LICENSE_CATEGORY_PERMISSIVE = "Permissive";
	public static String LICENSE_CATEGORY_WEAK_RECIPROCAL = "Weak Reciprocal";
	public static String LICENSE_CATEGORY_RECIPROCAL = "Reciprocal";
	
	public static String[] LICENSE_CAT_LIST = {LICENSE_CATEGORY_UNKNOWN, LICENSE_CATEGORY_PERMISSIVE, LICENSE_CATEGORY_WEAK_RECIPROCAL, LICENSE_CATEGORY_RECIPROCAL};
	
	
	// Error Keys
	public static String PROTEX_ERROR_MESSAGE_KEY = "protex-error-message";
	public static String CODE_CENTER_ERROR_MESSAGE_KEY = "code-center-error-message";
	
	// Component Analysis
	public static String PROTEX_TOTAL_FILES_METRIC_KEY = "protex-total-files";
	public static String PROTEX_TOTAL_PENDING_METRIC_KEY = "protex-total-pending-files";
	public static String PROTEX_TOTAL_NO_DISCOVERY_METRIC_KEY = "protex-total-no-discovery-files";
	public static String PROTEX_ASSOCIATED_PROJECT_KEY = "protex-project";
	public static String PROTEX_TOTAL_DISCOVERY_METRIC_KEY = "protex-total-discovery-files";
	public static String PROTEX_TOTAL_LIC_CONFLICT_METRIC_KEY = "protex-total-license-conflict-files";
	public static String PROTEX_ANALYZED_DATE_KEY = "protex-analyzed-date";
	
	// Components and related approvals.
	public static String COMP_LIST_APPROVED_JSON_KEY = "comp-list-approved-json";
	public static String COMP_LIST_REJECTED_JSON_KEY = "comp-list-rejected-json";
	public static String COMP_LIST_PENDING_JSON_KEY = "comp-list-pending-json";
	public static String COMP_LIST_NS_JSON_KEY = "comp-list-ns-json";
	
	public static String REQUESTS_PENDING_METRIC_KEY = "requests-pending";
	public static String REQUESTS_APPROVED_METRIC_KEY = "requests-approved";
	public static String REQUESTS_REJECTED_METRIC_KEY = "requests-rejected";
	public static String REQUESTS_NOT_SUBMITTED_METRIC_KEY = "requests-not-submitted";
	public static String CC_CUSTOM_ATTIBUTES_KEY = "custom-attributes";
	
	// Vulnerabilities
	
	public static String VULNERABILITY_HIGH = "vuln-high";
	public static String VULNERABILITY_MED = "vuln-med";
	public static String VULNERABILITY_LOW = "vuln-low";
	
	public static String VULNERABILITY_HIGH_JSON_KEY = "vuln-high-list-json";
	public static String VULNERABILITY_MED_JSON_KEY = "vuln-med-list-json";
	public static String VULNERABILITY_LOW_JSON_KEY = "vuln-low-list-json";
	
	// Licenses
	public static String LICENSE_BREAKDOWN = "license-breakdown";
	public static String LICENSE_BREAKDOWN_KEY_VALUE_PAIR = "license-breakdown-key-value";
	public static String LICENSE_BREAKDOWN_JSON_KEY = "license-breakdown-json";
	
	// Used on the license info widget
	public static String CC_APP_BOM_URL_KEY = "cc-app-bom-url";
	

}
