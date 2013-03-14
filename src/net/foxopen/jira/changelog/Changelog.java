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
    
    // Handle optional flags
    boolean setReleasedInJira = true;
    String objectCachePath = null;
    
    boolean skipNextArg = false;
    for (int i = 5; i < args.length; i++) {
      if (skipNextArg == true) {
        skipNextArg = false;
        continue;
      }
      
      String currentArg = args[i];
      
      if (currentArg.equals("--no-release")) {
        setReleasedInJira = false;
      } else if (currentArg.equals("--object-cache-path")) {
        skipNextArg = true;
        objectCachePath = args[i+1];
      }
    }
    
    JiraAPI jiraApi = new JiraAPI(jiraUsername, jiraPassword, jiraURL, setReleasedInJira);
    if (objectCachePath != null) {
      VersionInfoCache cache = new VersionInfoCache(jiraProjectKey, objectCachePath);
      jiraApi.setVersionInfoCache(cache);
    }
    jiraApi.fetchVersionDetails(jiraProjectKey, versionName);
    
    ChangelogBuilder clWriter = new ChangelogBuilder();
    clWriter.build(jiraApi.getVersionInfoList());
    clWriter.print();
    
    jiraApi.releaseVersion(jiraProjectKey, versionName);
    
    System.exit(0);
  }
}
