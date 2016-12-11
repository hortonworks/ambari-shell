/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sequenceiq.ambari.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.ambari.shell.model.AmbariContext;

/**
 * Commands related to users.
 *
 * @see com.sequenceiq.ambari.client.AmbariClient
 */
@Component
public class UsersCommands implements CommandMarker {

  private AmbariClient client;
  private AmbariContext context;

  @Autowired
  public UsersCommands(AmbariClient client, AmbariContext context) {
    this.client = client;
    this.context = context;
  }

  /**
   * Checks whether the users changepassword command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator("users changepassword")
  public boolean isUsersChangePasswordCommandAvailable() {
    return context.isConnectedToCluster();
  }

  /**
   * Change a user's password
   *
   * @return message indicating whether the password change was successful
   */
  @CliCommand(value = "users changepassword", help = "Change a user's password")
  public String changePassword(@CliOption(key = "user", mandatory = true, help = "Username") String user,
                               @CliOption(key = "oldpassword", mandatory = true, help = "Password of current user using Ambari Shell") String oldpassword,
                               @CliOption(key = "newpassword", mandatory = true, help = "New password") String newpassword) {
    String message;
    try {
      client.changePassword(user,oldpassword, newpassword,false);
      message = "Changed password for user " + user;
    } catch (Exception e) {
      message = "Could not change password for user " + user;
    }
    return message;
  }

}
