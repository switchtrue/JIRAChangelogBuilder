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
 *
 * @author apigram
 */
public class ChangelogTemplate {

  static HashMap<String, Object> scopes = new HashMap<String, Object>();

  /**
   * Generate and output a changelog based off a template file
   *
   * @param versions A collection of JIRA versions with their respective issues
   * @param output The output stream
   * @param templateFile The filename of the template file to use.
   */
  public static void createChangelog(List<VersionInfo> versions, Writer output, String templateFile) {
    // assemble the JSON hash map
    scopes.put("versions", versions);

    // Compile the required template and generate some output. This output will
    // either be piped to a file or copied into a FOX module.
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache template = mf.compile(templateFile);
    template.execute(output, scopes);
  }
}
