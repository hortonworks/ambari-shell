# Ambari Shell

[Ambari](http://ambari.apache.org/) is aimed to help provision/manage/monitor Hadoop clusters.
Although it has a [REST API](https://github.com/apache/ambari/blob/trunk/ambari-server/docs/api/v1/index.md)
right now the only way to use ambari is by usingh the web-application.

Ambari Shell's aim is to provide an interactive command line tool which supports:

- all functionality available through Ambari web-app
- context aware command availability
- tab completion
- required/optional parameter support

## Architecture

To implement ambari-shell the following options were evaluated:

- **python**: seems a natural fit as a few components of Ambari are written in python. Also
  an [Ambari python shell](https://cwiki.apache.org/confluence/display/AMBARI/Ambari+python+Shell)
  was announced on Ambari wiki page, but after a discussion with committers, it turned out
  to be abandoned path. The official [Ambari python client](https://cwiki.apache.org/confluence/display/AMBARI/Ambari+python+Client)
  hasn't been released, so the python option is eliminated.
- **go**: go would solve nicely the usual dependency hell (pip, gem, classpath, you name it), but unfortunately our
  team has no experience with it.
- **java**: The **write-once-run-everywhere** nature provides support for all OS. [Spring-Shell](http://docs.spring.io/spring-shell/docs/1.0.x/reference/htmlsingle/#preface)
  seems a natural fit as a base framework.

Spring-Shell is also battle tested in various Spring projects including:
- [Spring-Roo](http://projects.spring.io/spring-roo/): lightweight cli tool to aim Rapid Application Development
- [Spring-XD](http://docs.spring.io/spring-xd/docs/1.0.0.BUILD-SNAPSHOT/reference/html) a user-friendly
  front end for the REST API of Spring XD. Spring XD is a unified, distributed,
  service for data ingestion, real time analytics, batch processing, and data export.
- [Spring-Rest-Shell](https://github.com/spring-projects/rest-shell) a command-line shell that aims to make writing REST-based applications easier.
  Spring Rest-Shell itself would be enough to communicate against Ambari REST API, but we wanted a more
  Donamin Specific Language (DSL) nature of the command structure.

## Installation and usage

Ambari-Shell is distributed as a single-file executable jar. So no `ClassNotFound` should happen. The  **uber jar**
is generated with the help of spring-boot-maven-plugin available at: [Spring-Boot](http://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/htmlsingle/#executable-jar).
Spring-Boot also provides a helper to launch those jars: [JarLauncher](http://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/htmlsingle/#executable-jar-launching).

After compiling the project, the shell is ready to use (make sure you use Java 7 or above).

```
java -jar ambari-shell/target/ambari-shell-1.3.0-SNAPSHOT.jar --ambari.server=localhost --ambari.port=8080 --ambari.user=admin --ambari.password=admin
```

The `--ambari` options can be omitted if they are the default values otherwise you only need to specify the difference, e.g just the port is different

```
java -jar ambari-shell/target/ambari-shell-1.3.0-SNAPSHOT.jar --ambari.port=49178
```
```
    _                _                   _  ____   _            _  _
   / \    _ __ ___  | |__    __ _  _ __ (_)/ ___| | |__    ___ | || |
  / _ \  | '_ ` _ \ | '_ \  / _` || '__|| |\___ \ | '_ \  / _ \| || |
 / ___ \ | | | | | || |_) || (_| || |   | | ___) || | | ||  __/| || |
/_/   \_\|_| |_| |_||_.__/  \__,_||_|   |_||____/ |_| |_| \___||_||_|

Welcome to Ambari Shell. For assistance press tab or use the `hint` command.
```

## Implemented Commands

- **blueprint add** - Add a new blueprint with either --url or --file
- **blueprint defaults** - Adds the default blueprints to Ambari
- **blueprint list** - Lists all known blueprints
- **blueprint show** - Shows the blueprint by its id
- **cluster assign** - Assign host to host group
- **cluster autoAssign** - Auto assigns host to host groups (based on blueprint cardinality)
- **cluster build** - Starts to build a cluster
- **cluster create** - Create a cluster based on current blueprint and assigned hosts
- **cluster delete** - Delete the cluster
- **cluster preview** - Shows the currently assigned hosts
- **cluster reset** - Clears the host - host group assignments
- **debug off** - Stops showing the URL of the API calls
- **debug on** - Shows the URL of the API calls
- **exit** - Exits the shell
- **hello** - Prints a simple elephant to the console
- **help** - List all commands usage
- **hint** - Shows some hints
- **host components** - Lists the components assigned to the selected host
- **host focus** - Sets the useHost to the specified host
- **host list** - Lists the available hosts
- **quit** - Exits the shell
- **script** - Parses the specified resource file and executes its commands
- **service components** - Lists all services with their components
- **service list** - Lists the available services
- **services start** - Starts all the services
- **services stop** - Stops all the running services
- **tasks** - Lists the Ambari tasks
- **version** - Displays shell version

Please note that all commands are context aware - and are available only when it makes sense.
For example the `cluster create` command is not available until a `blueprint` has not been added or selected.
A good approach is to use the `hint` command - as the Ambari UI, this will give you hints about the available commands and the flow of creating or configuring a cluster.

*You can always use TAB for completion or available parameters.*

Example:

Once you logged in you can say `hello`.

                    .-.._
              __  /`     '.
           .-'  `/   (   a \
          /      (    \,_   \
         /|       '---` |\ =|
        ` \    /__.-/  /  | |
           |  / / \ \  \   \_\
           |__|_|  |_|__\

Initially there are no blueprints available - you cn add blueprints from file or URL. For your convenience we have added 2 blueprints as defaults.
You can get these blueprints by using the `blueprint defaults` command. The result is the following:
```
  BLUEPRINT              STACK
  ---------------------  -------
  multi-node-hdfs-yarn   HDP:2.0
  single-node-hdfs-yarn  HDP:2.0
```

Once the blueprints are available you can use them to create a cluster. You can use the following command: `cluster build --blueprint single-node-hdfs-yarn`.
Now that the blueprint is selected you have to assign the hosts to the available host groups.

Use `cluster assign --hostGroup host_group_1 --host server.ambari.com`.

You can always `cluster reset` or `cluster preview` to modify or check the configuration.
```
HOSTGROUP     HOST
  ------------  -----------------
  host_group_1  server.ambari.com
```

Once you are happy with you can choose `cluster create` to start building the cluster. Progress can be checked either at Amabri UI or using the `tasks` command.
```
  TASK                        STATUS
  --------------------------  -------
  HISTORYSERVER INSTALL       QUEUED
  ZOOKEEPER_SERVER START      PENDING
  ZOOKEEPER_CLIENT INSTALL    PENDING
  HDFS_CLIENT INSTALL         PENDING
  HISTORYSERVER START         PENDING
  NODEMANAGER INSTALL         QUEUED
  NODEMANAGER START           PENDING
  ZOOKEEPER_SERVER INSTALL    QUEUED
  YARN_CLIENT INSTALL         PENDING
  NAMENODE INSTALL            QUEUED
  RESOURCEMANAGER INSTALL     QUEUED
  NAMENODE START              PENDING
  RESOURCEMANAGER START       PENDING
  DATANODE START              PENDING
  SECONDARY_NAMENODE START    PENDING
  DATANODE INSTALL            QUEUED
  MAPREDUCE2_CLIENT INSTALL   PENDING
  SECONDARY_NAMENODE INSTALL  QUEUED
```

## Summary
To sum it up in less than two minutes watch this video:
https://asciinema.org/a/9783

## Known Issues
- Unfortunately Ambari often fails to install Ganglia due to network issues, either try again or remove that Ganglia from your blueprint.
- To use the ```cluster autoAssign``` feature at least 1 host group's name should start with slave_ e.g. ```slave_1```

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/da1aff134f981b790d7870262731514e "githalytics.com")](http://githalytics.com/sequenceiq/ambari-shell)


