package net.foxopen.jira.changelog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.StringWriter;

/**
 * Build the changelog as text from a given list of VersionInfo instances.
 * @author leonmi
 * 
 */
public class ChangelogBuilder
{
  private StringWriter changelogStringWriter_;
  
  /**
   * Iterates over a list of JIRA project versions and creates a changelog.
   * 
   * @param versionInfoList The list of JIRA versions to build the changelog for as VersionInfo objects.
   * The change log will be generated in the order that the VersionInfo objects are in the list.
   */
  public void build(List<VersionInfo> versionInfoList, String filename)
  {
    changelogStringWriter_ = new StringWriter();
		
		// build the changelog for the file using the file template.
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(filename));
			ChangelogTemplate.createChangelog(true, versionInfoList, writer);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			// catch and ignore because we don't care if the file doesn't exist or cannot be written
		}
		Logger.log("Building module changelog.");
		ChangelogTemplate.createChangelog(false, versionInfoList, changelogStringWriter_);
		changelogStringWriter_.flush();
  }
  
  /**
   * Prints the module-level changelog to the screen.
   */
  public void print()
  {
    System.out.println(changelogStringWriter_.toString().trim());
  }
	
	/**
	 * Gets the module-level changelog. Useful for unit testing
	 * @return 
	 */
	String getModuleChangelog() {
		return changelogStringWriter_.toString().trim();
	}
}
