/**
 * 
 */
package com.blackducksoftware.soleng.bdsplugin.model;

/**
 * The basic pojo to contain license reach and attribute information
 * @author Ari Kamen
 *
 */
public class LicensePOJO {
	
	private String licenseID = null;
	private String licenseReachString = null;
	private Integer licenseReachNumber = null;

	
	public LicensePOJO(String id)
	{
		licenseID = id;
	}
	
	public String getLicenseID() {
		return licenseID;
	}

	public void setLicenseID(String licenseID) {
		this.licenseID = licenseID;
	}

	public String getLicenseReachString() {
		return licenseReachString;
	}

	public void setLicenseReachString(String licenseReachString) {
		this.licenseReachString = licenseReachString;
	}

	public Integer getLicenseReachNumber() {
		return licenseReachNumber;
	}

	public void setLicenseReachNumber(Integer licenseReachNumber) {
		this.licenseReachNumber = licenseReachNumber;
	}



}
