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
    * `--changelog-file-name /some/path/filename`: The path on disk to the file you wish to output the file changelog to. If you do not use this, the file changelog will be written to changelog#.txt in the working directory by default (where # is the changelog file number).
    * `--eol-style (NATIVE|CRLF|LF)`: The type of line endings you wish the changelog files to use. Valid values are NATIVE (system line endings), CRLF (Windows line endings) or LF (UNIX line endings). If you do not use this, the changelogs will use the default system line endings (NATIVE).
