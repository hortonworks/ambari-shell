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
import org.springframework.context.ApplicationContext;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.ambari.shell.model.AmbariContext;
import com.sequenceiq.ambari.shell.model.Blueprint;

/**
 * Commands used in the shell. Does nothing more than executing and delegating the commands
 * to the Ambari Server via a Groovy based client.
 *
 * @see com.sequenceiq.ambari.client.AmbariClient
 */
@Component
public class AmbariCommands implements CommandMarker {

  @Autowired
  private AmbariContext context;
  @Autowired
  private ApplicationContext applicationContext;
  private AmbariClient client;

  /**
   * Checks if the connect command is available or not.
   * Always returns true because you can connect any time
   * to an Ambari Server.
   *
   * @return true, always
   */
  @CliAvailabilityIndicator({"connect"})
  public boolean isConnectCommandAvailable() {
    return true;
  }

  /**
   * Connects to an Ambari Server.
   *
   * @param host     hostname of the Ambari Server
   * @param port     port number which the Ambari Server listens on
   * @param user     username for authentication
   * @param password password for authentication
   * @return status response
   */
  @CliCommand(value = "connect", help = "Connects to an Ambari Server")
  public String connect(
    @CliOption(key = {"host"}, mandatory = false, help = "Hostname of the Ambari Server; default is: 'localhost'", unspecifiedDefaultValue = "localhost")
    String host,
    @CliOption(key = {"port"}, mandatory = false, help = "Port number of the Ambari listens on; default is: '8080'", unspecifiedDefaultValue = "8080")
    String port,
    @CliOption(key = {"user"}, mandatory = false, help = "Username for authorization; default is: 'admin'", unspecifiedDefaultValue = "admin")
    String user,
    @CliOption(key = {"password"}, mandatory = false, help = "Password of the user; default is: 'admin'", unspecifiedDefaultValue = "admin")
    String password) {
    try {
      context.setServerConnection(host, port, user, password);
      client = applicationContext.getBean("ambariClient", AmbariClient.class);
      context.setCluster(client.getClusterName());
      return "cluster:" + client.getClusterName() + "\n" + client.clusterList();
    } catch (Exception e) {
      return "Connection failure: " + e.getMessage();
    }
  }

  /**
   * Checks whether the tasks command is available or not.
   *
   * @return true if its available false otherwise
   */
  @CliAvailabilityIndicator({"tasks"})
  public boolean isTasksCommandAvailable() {
    return client != null;
  }

  /**
   * Prints the tasks of the Ambari Server.
   *
   * @param id id of the request
   * @return task list
   */
  @CliCommand(value = "tasks", help = "Lists the Ambari tasks")
  public String tasks(
    @CliOption(key = {"id"}, mandatory = false, help = "Id of the request; default is: 1", unspecifiedDefaultValue = "1")
    String id) {
    return client.taskList(id);
  }

  /**
   * Checks whether the hosts command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"hosts"})
  public boolean isHostsCommandAvailable() {
    return client != null;
  }

  /**
   * Prints the available hosts of the Ambari Server.
   *
   * @return host list
   */
  @CliCommand(value = "hosts", help = "Lists the available hosts")
  public String hosts() {
    return client.hostList();
  }

  /**
   * Checks whether the services command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"services"})
  public boolean isServicesCommandAvailable() {
    return client != null;
  }

  /**
   * Prints the available services of the Ambari Server.
   *
   * @return service list
   */
  @CliCommand(value = "services", help = "Lists the available services")
  public String services() {
    return client.serviceList();
  }

  /**
   * Checks whether the serviceComponents command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"serviceComponents"})
  public boolean isServiceComponentsCommandAvailable() {
    return client != null;
  }

  /**
   * Prints the service components of the Ambari Server.
   *
   * @return service component list
   */
  @CliCommand(value = "serviceComponents", help = "Lists all services with their components")
  public String serviceComponents() {
    return client.allServiceComponents();
  }

  /**
   * Checks whether the focus command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"focus"})
  public boolean isFocusCommandAvailable() {
    return client != null;
  }

  /**
   * Sets the focus to the specified host.
   *
   * @param host the host to set the focus to
   * @return status message
   */
  @CliCommand(value = "focus", help = "Sets the focus to the specified host")
  public String focus(
    @CliOption(key = {"host"}, mandatory = true, help = "hostname") String host) {
    context.setHost(host);
    return "Focus set to:" + host;
  }

  /**
   * Checks whether the hostComponents command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"hostComponents"})
  public boolean isHostComponentsCommandAvailable() {
    return context.getHost() != null;
  }

  /**
   * Prints the components which belongs to the host previously set the focus on.
   *
   * @return list of host components
   */
  @CliCommand(value = "hostComponents", help = "Lists the components assigned to the selected host")
  public String hostComponents() {
    return client.hostComponentList(context.getHost());
  }

  /**
   * Checks whether the blueprints command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"blueprints"})
  public boolean isBlueprintsCommandAvailable() {
    return client != null;
  }

  /**
   * Prints all the blueprints.
   *
   * @return list of blueprints
   */
  @CliCommand(value = "blueprints", help = "Lists all known blueprints")
  public String blueprints() {
    return client.blueprintList();
  }

  /**
   * Checks whether the debug on command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"debug on"})
  public boolean isDebugOnCommandAvailable() {
    return !client.isDebugEnabled();
  }

  /**
   * Turns the debug on. From now on users will see the URLs of the API calls.
   *
   * @return status message
   */
  @CliCommand(value = "debug on", help = "Shows the URL of the API calls")
  public String debugOn() {
    client.setDebugEnabled(true);
    return "debug enabled";
  }

  /**
   * Checks whether the debug off command is available or not.
   *
   * @return true if available false otherwise
   */
  @CliAvailabilityIndicator({"debug off"})
  public boolean isDebugOffCommandAvailable() {
    return client.isDebugEnabled();
  }

  /**
   * Turns the debug off. URLs are not visible anymore.
   *
   * @return status message
   */
  @CliCommand(value = "debug off", help = "Stops showing the URL of the API calls")
  public String debugOff() {
    client.setDebugEnabled(false);
    return "debug disabled";
  }

  @CliAvailabilityIndicator({"addBlueprint"})
  public boolean isAddBlueprintCommandAvailable() {
    return client != null;
  }

  @CliCommand(value = {"addBlueprint"})
  public String addBlueprint(
    @CliOption(key = {"type"}, mandatory = true, help = "Type of the blueprint") Blueprint blueprint,
    @CliOption(key = {"name"}, mandatory = true, help = "Name of the blueprint") String name,
    @CliOption(key = {"url"}, mandatory = false, help = "URL of a custom blueprint json") String url) {
    String result;
    if (blueprint.equals(Blueprint.CUSTOM)) {
      result = addCustomBlueprint(name, url);
    } else {
      result = addDefaultBlueprint(blueprint);
    }
    return result;
  }

  private String addCustomBlueprint(String name, String url) {
    String result;
    if (url == null) {
      result = "In case of custom blueprint please specify the url";
    } else {
      result = client.addBlueprint(name, url);
    }
    return result;
  }

  private String addDefaultBlueprint(Blueprint blueprint) {
    return client.addBlueprint(blueprint.getName(), null);
  }
}
