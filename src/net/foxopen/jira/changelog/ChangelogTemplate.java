/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;
import java.util.HashMap;
import java.util.List;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import com.github.mustachejava.*;

/**
 * Incorporates the base logic for Mustache templates for changelogs saved as 
 * either a file or into a FOX module.
 * @author apigram
 * @version 1.03.00
 */
public class ChangelogTemplate {
  static HashMap<String, Object> scopes = new HashMap<String, Object>();
	static String LF;
    
  /**
	 * Generate and output a changelog based off a template file.
	 * @param isFile If the changelog is for a file, set this parameter to true. Otherwise, set this parameter to false.
	 * @param issues A collection of JIRA issues with an identifier and changelog description.
	 * @param version The build version.
	 * @param output The output stream.
	 * @param templateFile The template file to use when generating the changelog.
	 * @param ending A value indicating the kind of newlines to be used in the changelog file.
	 */
	public static void createChangelog(boolean isFile, List<VersionInfo> versions, Writer output, String templateFile, LineEnding ending) {
		StringWriter out = new StringWriter();
		String buffer = null; // templated file content. This buffer is used to convert the line endings.
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache template;
		
		// assemble the JSON hash map
    scopes.put("versions", versions);

		// Compile the required template and generate some output. This output will 
		// either be piped to a file or copied into a FOX module.
		if (isFile) {
			template = mf.compile(templateFile);
		} else {
			template = mf.compile(templateFile);
		}
		template.execute(out, scopes);
		
		// grab the merged string
		buffer = out.toString();
		out.flush();
		
		// convert line endings accordingly, then output to the output stream
		switch (ending) {
			case WINDOWS:
				// use Windows method of newlines
				buffer = buffer.replace("\n", "\r\n");
				break;
			case NIX:
				// use UNIX method of newlines
				buffer = buffer.replace("\r\n", "\n");
				break;
			default:
				// use system method of newlines
				LF = System.getProperty("line.separator");
				buffer = buffer.replaceAll("\r?\n", LF);
				break;
		}
		try {
			output.write(buffer);
			output.flush();
		} catch (IOException e) {
			// leave blank as file will not exist
		}
	}
}
