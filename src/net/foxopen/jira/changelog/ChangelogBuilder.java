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
  
  /**
   * Iterates over a list of JIRA project versions and creates a changelog.
   * 
   * @param versionInfoList The list of JIRA versions to build the changelog for as VersionInfo objects.
   * The change log will be generated in the order that the VersionInfo objects are in the list.
   */
  public void build(List<VersionInfo> versionInfoList, String filename, String[] templates)
  {
		FileWriter writer = null;
		int fileIndex = 1;
		
		// build the changelog for the file using the file template.
		try {
			for (String t : templates) {
				if (t != null) {
					Logger.log("Writing " + filename + (fileIndex) + ".txt");
					writer = new FileWriter(filename + (fileIndex++) + ".txt");
					ChangelogTemplate.createChangelog(true, versionInfoList, writer, t);
					writer.flush();
					writer.close();
				}
			}
		}
		catch (IOException e) {
			// catch and ignore because we don't care if the file doesn't exist or cannot be written
		}
  }
}
