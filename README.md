JIRAChangelogBuilder
====================

Communicates with JIRA and generates a change log for a given JIRA Project Version

Usage
-----

1) Create a file that contains the JIRA version label to build, this file can contain any number of lines starting with # (these are treated as comments) and any number of empty lines. The first non-comment, non-empty line will be trimmed and used as the JIRA project version name.

2) From the command line:

    java -jar jira-changelog-builder.jar <version_file_path> <JIRA_project_name> <JIRA_URL>
            <JIRA_username> <JIRA_password> [<path_to_cache>]
  
Where all arguments are used as follows:
  
  *  `<version_file_path>`: The fully qualified path in which to find the version file as described in step 1.
  *  `<JIRA_project_name>`: The name of the project in JIRA.
  *  `<JIRA_URL>`: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).
  *  `<JIRA_username>`: The username used to log into JIRA.
  *  `<JIRA_password>`: The password used to log into JIRA.
  *  `<path_to_cache>` (optional): The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.