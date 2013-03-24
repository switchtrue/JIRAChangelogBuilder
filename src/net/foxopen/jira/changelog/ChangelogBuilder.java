package net.foxopen.jira.changelog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author leonmi
 *
 * Build the changelog as text from a given list of VersionInfo instances.
 */
public class ChangelogBuilder
{
  private final String LS = System.getProperty("line.separator");
  private StringBuilder changelogStringBuilder_;
  
  /**
   * Iterates over a list of JIRA project versions and creates a changelog.
   * 
   * @param versionInfoList the list of JIRA versions to build the changelog for as VersionInfo objects.
   * The change log will be generated in the order that the VersionInfo objects are in the list.
   */
  public void build(List<VersionInfo> versionInfoList)
  {
    changelogStringBuilder_ = new StringBuilder();
   
    for (VersionInfo vi : versionInfoList) {
      if (vi.hasIssues()) {
        buildChangelogHeader(vi.getReleaseDate(), vi.getName(), vi.getDescription());
        for (String issue : vi.getIssueList()) {
          buildChangelogItem(issue);
        }
        finaliseChangelog();
      }
    }
  }
  
  /**
   * Prints the final changelog to the screen.
   */
  public void print()
  {
    System.out.println(changelogStringBuilder_.toString().trim());
  }
  
  /**
   * Generate and format a header for each version within the changelog consisting of the
   * release date, version name and description (if there is one).
   * 
   * It will be produced in the format:
   * <code>
   * =================================================================
   * 21-05-2012  REL-0.1.003
   * This is a milestone release of the new system look and feel.
   * -----------------------------------------------------------------
   * </code>
   * 
   * @param date The release date of the build.
   * @param versionName the name of the build.
   * @param description the description of build.
   */
  private void buildChangelogHeader(Date date, String versionName, String description)
  {
    SimpleDateFormat sdf = new SimpleDateFormat();
    sdf.applyPattern("dd-MM-yyyy");
    String formattedDate = sdf.format(date);
    
    changelogStringBuilder_.append("=================================================================");
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(formattedDate + "  " + versionName);
    if (description != null) {
      changelogStringBuilder_.append(LS);
      changelogStringBuilder_.append(description);
    }
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append("-----------------------------------------------------------------");
    changelogStringBuilder_.append(LS); 
  }
  
  /**
   * Generate and format a single change for a given build. 
   * 
   * @param description The description of a single change made in this build.
   */
  private void buildChangelogItem(String description)
  {
    String formattedDescription = "    - " + description;
    
    // If the change log has new lines in it, pad the new lines with white space equal to the indent and issuse key
    // so that the changelog description for a given issue lines up neatly.
    if (description.indexOf("\n") > 0) {
      int padding = formattedDescription.indexOf("] ") + 2;
      
      String whitespace = "\n";
      for (int i = 0; i < padding; i++) {
        whitespace += " ";
      }
      
      formattedDescription = formattedDescription.replaceAll("\n", whitespace);
    }

    changelogStringBuilder_.append(formattedDescription.trim());
    changelogStringBuilder_.append(LS);
  }
  
  /**
   * After a changelog has been written for a given build call this to tidy up spacing between builds
   * in the changelog.
   */
  private void finaliseChangelog()
  {
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(LS);
    changelogStringBuilder_.append(LS);
  }
}
