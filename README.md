[![Stories in Ready](https://badge.waffle.io/mleonard87/jirachangelogbuilder.png?label=ready&title=Ready)](https://waffle.io/mleonard87/jirachangelogbuilder)
JIRAChangelogBuilder
====================

Communicates with [Atlassians JIRA](https://www.atlassian.com/software/jira) and generates a changelog based on a customisable template file for a given [JIRA query (JQL)](https://confluence.atlassian.com/display/JIRA/Advanced+Searching).

Compilation
-----------

Run 'mvn install' from the base directory to generate build/jira-changelog-builder.jar. This will also automatically execute the bundled test case.

Usage
-----

From the command line:

    java -jar jira-changelog-builder.jar <JIRA_URL> <JIRA_username> <JIRA_password> <JIRA_project_key> <version> <template_list> [<flags>]
  
Where the arguments are used as follows:
  
  *  `<JIRA_URL>`: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).
  *  `<JIRA_username>`: The username used to log into JIRA.
  *  `<JIRA_password>`: The password used to log into JIRA.
  *  `<JIRA_project_key>`: The key of the project in JIRA.
  *  `<version>`: Specifies up to which version the changelog should be generated.
  *  `<template_root>`: The path on disk to the directory containing the mustache template files. All files in `<template_list>` are relative to this path.
  *  `<template_list>`: A CSV list of paths to template files. Each templated changelog is saved into a new file which can be processed at a later stage.
  *  `<flags>` (optional): One or more of the following flags:
    * `--jql "some arbitrary JQL"`: Append the given JQL to the issue filter. eg status = "Ready for Build".
    * `--object-cache-path /some/path`: The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.
    * `--debug`: Print debug/logging information to standard out. This will also force errors to go to the standard out and exit with code 0 rather than 1.
    * `--changelog-description-field "field_name"`: The name of the field in JIRA you wish to use as the changelog description field. If you do not use this, it will default to the summary field.
    * `--changelog-file-name /some/path/filename`: A CSV list of paths on disk to the files you wish to output the file changelogs to. If you do not use this, the file changelog will be written to changelog#.txt in the working directory by default (where # is the changelog file number).
    * `--eol-style (NATIVE|CRLF|LF)`: The type of line endings you wish the changelog files to use. Valid values are NATIVE (system line endings), CRLF (Windows line endings) or LF (UNIX line endings). If you do not use this, the changelogs will use the default system line endings (NATIVE).
    * `--version-starts-with "Version name prefix"`: Only display versions in the changelog that have a name starting with 'Version name prefix'. This cannot be used with --version-less-than-or-equal. This is useful for restricting what goes in the changelog if you are producing different version side-by-side. For example, if 'Version name prefix' is "ACME_1.3" then "ACME_1.3.1", "ACME_1.3.5" and "ACME_1.3.8" would all match whilst "ACME_1.1.8" and "ACME_1.4.9" would not.
    * `--version-less-than-or-equal "Version name"`: Only display versions in the changelog that have a name less than or equal to 'Version name'. This cannot be used with --version-starts-with. This uses a Java string comparison (compareTo). This is useful for restricting what goes in the changelog if you are producing different version side-by-side. For example if 'Version name' is "ACME_1.3.8" then "ACME_1.3.1", "ACME_1.3.5" and "ACME_1.1.8" would all match whilst "ACME_1.3.9" and "ACME_1.4.9" would not.
  
Managing the Cache
------------------

When specifying `--object-cache-path` the Java objects used to store the changelog information for a single version are serialized. The cache will always be used for any version other that the current version that has been specified in `<version>` however sometimes you need to rebuild an older version from JIRA (i.e. the version has changed in some way). This can be done simply by removing the serialised file, this fill will be found in the path specified by `--object-cache-path` with a file name of `<project>_<version>.ser` where `<project>` and `<version>` match the command line agruments. The JIRAChangelogBuilder will then regenerate these as required.

Testing
-------

In order to execute the unit tests properly (and build/install the program), you must create a file named testing.properties and place it in the base directory. In this file, add the following values:
  
  * `url = <URL>` where `<URL>` is the web address of the JIRA instance.
  * `username = <username>` where `<username>` is the username of a user that can access the JIRA instance.
  * `password = <password>` where `<password>` is the password for the specified user.
  * `project = <project>` where `<project>` is the identifier (key) of the JIRA project.
  * `version = <version>` where `<version>` is the version up to which the changelog should be generated.
  * `versionstartswith = <starts_with>` where `<starts_with>` is a substring (prefix) of a version for which the write will match on to decide which version to include. For example, if `<starts_with>` is "ACME_1.3" then "ACME_1.3.1", "ACME_1.3.5" and "ACME_1.3.8" would all match whilst "ACME_1.1.8" and "ACME_1.4.9" would not.
  * `versionlessthanorequal = <less_than_or_equal>` where `<less_than_or_equal>` is a version name that you want to compare as a string and only include versions with a name less than or equal to this. For example if `<less_than_or_equal>` is "ACME_1.3.8" then "ACME_1.3.1", "ACME_1.3.5" and "ACME_1.1.8" would all match whilst "ACME_1.3.9" and "ACME_1.4.9" would not.

Tests can be manually executed by running 'mvn test' from the base directory.
