/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;
import java.util.HashMap;
import java.util.List;
import java.io.Writer;
import java.io.IOException;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

/**
 * Incorporates the base logic for Mustache templates for changelogs saved as 
 * either a file or into a FOX module.
 * @author apigram
 */
public class ChangelogTemplate {
  public enum TemplateSelector {
    FILE,
    MODULE
  }
    
  public class Issue {
    public String name; // JIRA id
		public String description; // changelog description field
  }
    
  HashMap<String, Object> scopes;
    
  public void createChangelog(boolean isFile, List<Issue> issues, String version, Writer output) throws IOException {
		// assemble the JSON hash map
    scopes.put("version", version);
    for (Issue issue : issues) {
      scopes.put("issue", issue);
    }
		
		DefaultMustacheFactory mf = new DefaultMustacheFactory();
		Mustache template = null;
		if (isFile) {
			template = mf.compile("file.mustache");
		} else {
			template = mf.compile("module.mustache");
		}
		
		template.execute(output, mf).flush();
	}
}
