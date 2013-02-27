package net.foxopen.jira.changelog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ChangelogBuilder
{
  private final String LS = System.getProperty("line.separator");
  private StringBuilder changelogStringBuilder_;
  
  public void build(LinkedList<VersionInfo> versionInfoList, String versionLabel)
  {
    changelogStringBuilder_ = new StringBuilder();
   
    for (VersionInfo vi : versionInfoList) {
      buildChangelogHeader(vi.getReleaseDate(), vi.getName(), vi.getDescription());
      for (String issue : vi.getIssueList()) {
        buildChangelogItem(issue);
      }
      finaliseChangelog();
    }
  }
  
  public void print()
  {
    System.out.println(changelogStringBuilder_.toString());
  }
  
  private void buildChangelogHeader(Date date, String versionLabel, String description)
  {
    SimpleDateFormat sdf = new SimpleDateFormat();
    sdf.applyPattern("dd-MM-yyyy");
    String formattedDate = sdf.format(date);
    
    changelogStringBuilder_.append("=================================================================");
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(formattedDate + "  " + versionLabel);
    if (description != null) {
      changelogStringBuilder_.append(LS);
      changelogStringBuilder_.append(description);
    }
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append("-----------------------------------------------------------------");
    changelogStringBuilder_.append(LS); 
  }
  
  private void buildChangelogItem(String description)
  {
    changelogStringBuilder_.append("    - " + description);
    changelogStringBuilder_.append(LS);
  }
  
  private void finaliseChangelog()
  {
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(LS);
  }
}
