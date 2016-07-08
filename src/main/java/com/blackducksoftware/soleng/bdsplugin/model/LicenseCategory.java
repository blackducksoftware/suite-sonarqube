/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.soleng.bdsplugin.model;

/**
 * This is the license category bean that contains the following information: -
 * What category name - Number of times it shows up - Color
 */
public class LicenseCategory {

	private String color;
	private String categoryName;
	private Integer count = 0;

	public LicenseCategory(final String catName, final String color) {
		this.categoryName = catName;
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getCount() {
		return count;
	}

	public void incrementCount() {
		++count;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("License Category Name: " + categoryName);
		sb.append("\n");
		sb.append("Count: " + count);
		return sb.toString();
	}

}
