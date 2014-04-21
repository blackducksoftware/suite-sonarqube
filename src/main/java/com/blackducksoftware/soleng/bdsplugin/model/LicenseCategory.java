package com.blackducksoftware.soleng.bdsplugin.model;

/**
 * This is the license category bean that contains the following information:
 * - What category name
 * - Number of times it shows up
 * - Color
 * @author akamen
 *
 */
public class LicenseCategory {
	
	private String color;
	private String categoryName;
	private Integer count = 0;
	
	public LicenseCategory(String catName, String color)
	{
		this.categoryName = catName;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getCount() {
		return count;
	}
	public void incrementCount() {
		++count;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("License Category Name: " + categoryName);
		sb.append("\n");
		sb.append("Count: " + count);
		return sb.toString();
	}

}
