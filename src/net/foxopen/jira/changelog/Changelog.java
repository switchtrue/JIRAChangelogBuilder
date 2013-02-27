package net.foxopen.jira.changelog;

public class Changelog
{
  public static void main(String[] args) 
  { 
    final String versionFilePath = args[0];
    final String jiraProjectName = args[1];
    final String jiraURL         = args[2];
    final String jiraUsername    = args[3];
    final String jiraPassword    = args[4];
     
    String versionLabel = VersionReader.getRelease(versionFilePath);
    
    JiraAPI jiraApi = new JiraAPI(jiraUsername, jiraPassword, jiraURL);
    jiraApi.fetchVersionDetails(jiraProjectName, versionLabel);
    
    ChangelogBuilder clWriter = new ChangelogBuilder();
    clWriter.build(jiraApi.getVersionInfoList(), versionLabel);
    clWriter.print();
    
    System.exit(0);
  }
}
