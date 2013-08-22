JIRAChangelogBuilder
====================

Communicates with JIRA and generates a change log for a given JIRA Project Version.
Downloads are available at http://mleonard87.github.io/JIRAChangelogBuilder/releases/

Compilation
-----------

Run 'mvn install' from the base directory to generate build/jira-changelog-builder.jar. This will also automatically execute the bundled test case.

Usage
-----

From the command line:

    java -jar jira-changelog-builder.jar <JIRA_URL> <JIRA_username> <JIRA_password> <JIRA_project_name> <version> <template_list> [<flags>]
  
Where the arguments are used as follows:
  
  *  `<JIRA_URL>`: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).
  *  `<JIRA_username>`: The username used to log into JIRA.
  *  `<JIRA_password>`: The password used to log into JIRA.
  *  `<JIRA_project_name>`: The name of the project in JIRA.
  *  `<version>`: The name of the version this changelog is for.
  *  `<template_list>`: A CSV list of paths to template files. Each templated changelog is saved into a new file which can be processed at a later stage.
  *  `<flags>` (optional): One or more of the following flags:
    * `--jql "some arbitrary JQL"`: Append the given JQL to the issue filter. eg status = "Ready for Build"'
    * `--object-cache-path /some/path`: The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.
    * `--debug`: Print debug/logging information to standard out. This will also force errors to go to the standard out and exit with code 0 rather than 1.
    * `--changelog-file-name /some/path/filename`: A CSV list of paths on disk to the files you wish to output the file changelogs to. If you do not use this, the file changelog will be written to changelog#.txt in the working directory by default (where # is the changelog file number).

Testing
-------

In order to execute the unit tests properly (and build/install the program), you must create a file named credentials.properties and place it in the base directory. In this file, add the following values:
  * `url = <URL>` where `<URL>` is the web address of the JIRA instance.
  * `username = <username>` where `<username>` is the username of a user that can access the JIRA instance.
  * `password = <password>` where `<password>` is the password for the specified user.
  * `project = <project>` where `<project>` is the identifier of the JIRA project.
  * `version = <version>` where `<version>` is the number of the version for which to generate the changelogs.

Tests can be manually executed by running 'mvn test' from the base directory.
