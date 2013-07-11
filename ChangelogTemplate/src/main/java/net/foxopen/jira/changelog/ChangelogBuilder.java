package net.foxopen.jira.changelog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.io.StringWriter;

/**
 * Build the changelog as text from a given list of VersionInfo instances.
 * @author leonmi
 * 
 */
public class ChangelogBuilder
{
  private final String LS = System.getProperty("line.separator");
  private StringWriter changelogStringWriter_;
  
  /**
   * Iterates over a list of JIRA project versions and creates a changelog.
   * 
   * @param versionInfoList The list of JIRA versions to build the changelog for as VersionInfo objects.
   * The change log will be generated in the order that the VersionInfo objects are in the list.
   */
  public void build(List<VersionInfo> versionInfoList)
  {
    changelogStringWriter_ = new StringWriter();
		ChangelogTemplate.createChangelog(false, versionInfoList, changelogStringWriter_);
		changelogStringWriter_.flush();
  }
  
  /**
   * Prints the final changelog to the screen.
   */
  public void print()
  {
    System.out.println(changelogStringWriter_.toString().trim());
  }
}
