/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;
import java.util.HashMap;
import java.util.List;
import java.io.Writer;
import com.github.mustachejava.*;

/**
 * Incorporates the base logic for Mustache templates for changelogs saved as 
 * either a file or into a FOX module.
 * @author apigram
 */
public class ChangelogTemplate {
  static HashMap<String, Object> scopes = new HashMap<String, Object>();
    
  /**
	 * Generate and output a changelog based off a template file
	 * @param isFile If the changelog is for a file, set this parameter to true. Otherwise, set this parameter to false.
	 * @param issues A collection of JIRA issues with an identifier and changelog description
	 * @param version The build version
	 * @param output The output stream
	 */
	public static void createChangelog(boolean isFile, List<VersionInfo> versions, Writer output) {
		// assemble the JSON hash map
    scopes.put("versions", versions);
		
		// Compile the required template and generate some output. This output will 
		// either be piped to a file or copied into a FOX module.
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache template;
		if (isFile) {
			template = mf.compile("file.mustache");
		} else {
			template = mf.compile("module.mustache");
		}
		template.execute(output, scopes);
	}
}
