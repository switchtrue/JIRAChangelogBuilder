JIRAChangelogBuilder
====================

Communicates with JIRA and generates a change log for a given JIRA Project Version

Compilation
-----------

Run 'ant' from the base directory to generate build/jira-changelog-builder.jar

Usage
-----

From the command line:

    java -jar jira-changelog-builder.jar <version> <JIRA_project_name> <JIRA_URL>
            <JIRA_username> <JIRA_password> [<flags>]
  
Where the arguments are used as follows:
  
  *  `<version>`: The name of the version this changelog is for.
  *  `<JIRA_project_name>`: The name of the project in JIRA.
  *  `<JIRA_URL>`: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).
  *  `<JIRA_username>`: The username used to log into JIRA.
  *  `<JIRA_password>`: The password used to log into JIRA.
  *  `<flags>` (optional): One or more of the following flags:
    * `--object-cache-path /some/path`: The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.
    * `--issue-type-ignore CSV,issuetype,list`: A CSV of issue types to ignore when building the changelog.
    * `--debug`: Print debug/logging information to standard out. This will also force errors to go to the standard out and exit with code 0 rather than 1.