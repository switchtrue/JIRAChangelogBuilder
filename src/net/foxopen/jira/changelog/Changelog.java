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
   * --object-cache-path
   */
  
  public static void main(String[] args) 
  { 
    
    if ( args.length < 5 ) {
      System.out.println("Not enough arguments given.");
      System.out.println("Usage:");
      System.out.println("java -jar jira-changelog-builder.jar <version> <JIRA_project_name> <JIRA_URL> <JIRA_username> <JIRA_password> [<flags>]");
      System.out.println("<version>: The name of the version this changelog is for.");
      System.out.println("<JIRA_project_name>: The name of the project in JIRA.");
      System.out.println("<JIRA_URL>: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).");
      System.out.println("<JIRA_username>: The username used to log into JIRA.");
      System.out.println("<JIRA_password>: The password used to log into JIRA.");
      System.out.println("<flags> (optional): One or more of the following flags:");
      System.out.println("\t--object-cache-path /some/path: The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.");
      System.out.println("\t--issue-type-ignore CSV,issuetype,list: A CSV of issue types to ignore when building the changelog.");
      System.out.println("\t--debug: Print debug/logging information to standard out. This will also force errors to go to the standard out and exit with code 0 rather than 1.");
      System.exit(1);
    }
    
    int currentArgument = 0;
    final String versionName     = args[currentArgument++];
    final String jiraProjectKey  = args[currentArgument++];
    final String jiraURL         = args[currentArgument++];
    final String jiraUsername    = args[currentArgument++];
    final String jiraPassword    = args[currentArgument++];
    
    // Handle optional flags
    String issueTypeIgnoreList = "";
    String objectCachePath = null;
    for (; currentArgument < args.length; currentArgument++) {
      if (args[currentArgument].equals("--debug")) {
        Logger.enable();
        Logger.log("--debug flag found. Debug logging enabled.");
      } else if (args[currentArgument].equals("--issue-type-ignore")) {
        issueTypeIgnoreList = args[++currentArgument];
        Logger.log("--issue-type-ignore flag found. Ignoring issue types: " + issueTypeIgnoreList);
      } else if (args[currentArgument].equals("--object-cache-path")) {
        objectCachePath = args[++currentArgument];
        Logger.log("--object-cache-path flag found. Using " + objectCachePath + " as the object cache.");
      } else {
        Logger.err("Unknown argument: " + args[currentArgument]);
        System.exit(2);
      }
    }
    
    Logger.log("Starting with parameters: " +
        "\n  Version: " + versionName + 
        "\n  JIRA Project Key: " + jiraProjectKey + 
        "\n  JIRA URL: " + jiraURL + 
        "\n  JIRA username: " + jiraUsername + 
        "\n  JIRA password: " + jiraPassword.substring(0, 1) + "*****" + jiraPassword.substring(jiraPassword.length() - 1)
        );
    
    JiraAPI jiraApi = new JiraAPI(jiraUsername, jiraPassword, jiraURL, issueTypeIgnoreList);
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
    
    Logger.log("Done - Success!");
    System.exit(0);
  }
}
