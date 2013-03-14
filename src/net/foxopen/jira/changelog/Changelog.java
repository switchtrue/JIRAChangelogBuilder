package net.foxopen.jira.changelog;

public class Changelog
{
  /**
   * Generate a change log based off a project and version in JIRA.
   * @param args
   * <code>
   * Usage:
   * java -jar jira-changelog-builder.jar <version> <JIRA_project_name> <JIRA_URL> <JIRA_username> <JIRA_password>
   * 
   * <version>: The name of the version this changelog is for.
   * <JIRA_project_name>: The name of the project in JIRA.
   * <JIRA_URL>: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).
   * <JIRA_username>: The username used to log into JIRA.
   * <JIRA_password>: The password used to log into JIRA.
   * </code>
   * 
   * --issue-type-ignore Test
   * --debug
   * --no-update-jira
   * --object-cache-path
   */
  public static void main(String[] args) 
  { 
    final String versionName = args[0];
    final String jiraProjectKey  = args[1];
    final String jiraURL         = args[2];
    final String jiraUsername    = args[3];
    final String jiraPassword    = args[4];
    
    for (int i = 5; i < args.length; i++) {
      if (args[i].equals("--debug")) {
        Logger.enable();
        Logger.log("--debug flag found. Debug logging enable.");
      }
    }
    
    // Handle optional flags
    String issueTypeIgnoreList = "";
    boolean setReleasedInJira = true;
    String objectCachePath = null;
    
    boolean skipNextArg = false;
    for (int i = 5; i < args.length; i++) {
      if (skipNextArg == true) {
        skipNextArg = false;
        continue;
      }
      
      String currentArg = args[i];
      
      if (currentArg.equals("--issue-type-ignore")) {
        issueTypeIgnoreList = args[i+1];
        skipNextArg = true;
        Logger.log("--issue-type-ignore flag found. Ignoring issue types: " + issueTypeIgnoreList);
      } else if (currentArg.equals("--no-release")) {
        setReleasedInJira = false;
        Logger.log("--no-release flag found. JIRA version will not be marked as released or have release date set.");
      } else if (currentArg.equals("--object-cache-path")) {
        skipNextArg = true;
        objectCachePath = args[i+1];
        Logger.log("--object-cache-path flag found. Using " + objectCachePath + " as the object cache.");
      }
    }
    
    Logger.log("Starting with parameters: " +
        "\n  Version: " + versionName + 
        "\n  JIRA Project Key: " + jiraProjectKey + 
        "\n  JIRA URL: " + jiraURL + 
        "\n  JIRA username: " + jiraUsername + 
        "\n  JIRA password: " + jiraPassword.substring(0, 1) + "*****" + jiraPassword.substring(jiraPassword.length() - 1)
        );
    
    JiraAPI jiraApi = new JiraAPI(jiraUsername, jiraPassword, jiraURL, setReleasedInJira, issueTypeIgnoreList);
    if (objectCachePath != null) {
      VersionInfoCache cache = new VersionInfoCache(jiraProjectKey, objectCachePath);
      jiraApi.setVersionInfoCache(cache);
    }
    jiraApi.fetchVersionDetails(jiraProjectKey, versionName);
    
    ChangelogBuilder clWriter = new ChangelogBuilder();
    Logger.log("Building changelog.");
    clWriter.build(jiraApi.getVersionInfoList());
    Logger.log("Writing changelog to standard out.");
    clWriter.print();
    
    jiraApi.releaseVersion(jiraProjectKey, versionName);
    Logger.log("Done - Success!");
    System.exit(0);
  }
}
