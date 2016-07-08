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
package com.blackducksoftware.soleng.bdsplugin.config;

import com.blackducksoftware.tools.commonframework.core.config.user.CommonUser;

public class BDSPluginUser implements CommonUser {

    private static final long serialVersionUID = 1L;

    private String server;

    private String user;

    private String password;

    public BDSPluginUser(String server, String user, String password) throws Exception
    {
        this.server = server;
        if (server == null || server.isEmpty()) {
            throw new Exception("Server URL cannot be empty!");
        }
        this.user = user;
        if (user == null || user.isEmpty()) {
            throw new Exception("User name cannot be empty!");
        }
        this.password = password;
        if (password == null || password.isEmpty()) {
            throw new Exception("Password cannot be empty!");
        }
    }

    @Override
    public void setServer(String servername) {
        server = servername;

    }

    @Override
    public void setUserName(String username) {
        user = username;

    }

    @Override
    public void setPassword(String password) {
        this.password = password;

    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getUserName() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
