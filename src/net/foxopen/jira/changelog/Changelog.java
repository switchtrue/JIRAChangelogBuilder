package net.foxopen.jira.changelog;

import java.io.File;

/**
 * Main class for creating a changelog from JIRA.
 * @author leonmi
 */
public class Changelog
{
	/**
	 * Show usage of the application.
	 */
  public static void showUsage()
  {
    System.out.println("Usage:");
    System.out.println("java -jar jira-changelog-builder.jar <JIRA_URL> <JIRA_username> <JIRA_password> <JIRA_project_name> <version> <template_list> [<flags>]");
    System.out.println("<JIRA_URL>: The URL of the JIRA instance (e.g. https://somecompany.atlassian.net).");
    System.out.println("<JIRA_username>: The username used to log into JIRA.");
    System.out.println("<JIRA_password>: The password used to log into JIRA.");
    System.out.println("<JIRA_project_name>: The name of the project in JIRA.");
    System.out.println("<version>: The name of the version this changelog is for.");
		System.out.println("<template_list>: A CSV list of paths to template files. Each templated changelog is saved into a new file which can be processed at a later stage.");
    System.out.println("<flags> (optional): One or more of the following flags:");
    // TODO: If this JQL causes no issues to be returned, it causes a hard error. Handle this more nicely.
    System.out.println("\t--jql 'some arbitrary JQL': Append the given JQL to the issue filter. eg 'status = \"Ready for Build\"'");
    System.out.println("\t--object-cache-path /some/path: The path on disk to the cache, if you do not use this, no cache will be used. Using a cache is highly recommended.");
    System.out.println("\t--debug: Print debug/logging information to standard out. This will also force errors to go to the standard out and exit with code 0 rather than 1.");
		System.out.println("\t--changelog-file-name /some/path/file: A CSV list of paths on disk to the files you wish to output the changelogs to. If you do not use this, the file changelog will be written to changelog.txt in the working directory by default (where # is the changelog file number). If this is specified, the same number of paths as the template files must be specified.");
		System.out.println("\t--eol-style (NATIVE|CRLF|LF): The type of line endings you wish the changelog files to use. Valid values are NATIVE (system line endings), CRLF (Windows line endings) or LF (UNIX line endings). If you do not use this, the changelogs will use the default system line endings.");
	}
  
	/**
	 * Main function
	 * @param args Arguments passed in from the command line
	 */
  public static void main(String[] args) 
  { 
    if ( args.length == 1 &&  args[0].equals("--help")) {
      showUsage();
      System.exit(0);
    }
    if ( args.length < 6 ) {
      System.out.println("Not enough arguments given.");
      showUsage();
      System.exit(1);
    }
    
    int currentArgument = 0;
    final String jiraURL         = args[currentArgument++];
    final String jiraUsername    = args[currentArgument++];
    final String jiraPassword    = args[currentArgument++];
    final String jiraProjectKey  = args[currentArgument++];
    final String versionName     = args[currentArgument++];
		final String templateList		 = args[currentArgument++];
    
		String[] templates = templateList.split(",");
    
    // Handle optional flags
    String jql = "";
		String filenameList = null;
		String files[] = null;
    String objectCachePath = null;
		LineEnding ending = LineEnding.NATIVE; // default to native line endings
    for (; currentArgument < args.length; currentArgument++) {
      try {
        if (args[currentArgument].equals("--debug")) {
          Logger.enable();
          Logger.log("--debug flag found. Debug logging enabled.");
        } else if (args[currentArgument].equals("--jql")) {
					// extract the JQL string, replace the *s with spaces, and replace 
					// brackets with quotation marks (maven strips quotation marks)
          jql = args[++currentArgument];
					jql = jql.replaceAll("_", " ");
					jql = jql.replaceAll("(\\[|\\])", "'");
          Logger.log("--jql flag found. Appending JQL: " + jql);
        } else if (args[currentArgument].equals("--object-cache-path")) {
          objectCachePath = args[++currentArgument];
          Logger.log("--object-cache-path flag found. Using " + objectCachePath + " as the object cache.");
        } else if (args[currentArgument].equals("--changelog-file-name")) {
					filenameList = args[++currentArgument];
					files = filenameList.split(",");
					if (files.length != templates.length) {
						Logger.err("Output file list does not match template file list.");
						System.exit(2);
					}
					Logger.log("--changelog-file-name found. Using " + filenameList + " as changelog files.");
				} else if (args[currentArgument].equals("--eol-style")) {
					ending = LineEnding.getEnding(args[++currentArgument]);
					if (ending == null) {
						// invalid style, log error and terminate
						Logger.err("Unknown line ending style flag.");
						System.exit(4);
					}
				} else {
          Logger.err("Unknown argument: " + args[currentArgument]);
          System.exit(2);
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        // Assuming this has come from args[++currentArgument] in the above try block
        Logger.err("Malformed arguments. '" + args[currentArgument-1] + "' requires a following argument.");
        System.exit(3);
      }
    }
    
    Logger.log("Starting with parameters: " +
        "\n  Version: " + versionName + 
        "\n  JIRA Project Key: " + jiraProjectKey + 
        "\n  JIRA URL: " + jiraURL + 
        "\n  JIRA username: " + jiraUsername + 
        "\n  JIRA password: " + jiraPassword.substring(0, 1) + "*****" + jiraPassword.substring(jiraPassword.length() - 1) +
				"\n  Template files: " + templateList
        );
		
		File f;
		for (int i = 0; i < templates.length; i++) {
			f = new File(templates[i]);
			if (!f.exists()) {
				Logger.err("Template file " + f.getName() + " does not exist!");
				System.exit(2);
			}
		}
    
    JiraAPI jiraApi = new JiraAPI(jiraUsername, jiraPassword, jiraURL, jql);
    if (objectCachePath != null) {
      VersionInfoCache cache = new VersionInfoCache(jiraProjectKey, objectCachePath);
      jiraApi.setVersionInfoCache(cache);
    }
    jiraApi.fetchVersionDetails(jiraProjectKey, versionName);
    
    ChangelogBuilder clWriter = new ChangelogBuilder();
    Logger.log("Building changelog files.");
		
		if (filenameList == null) {
			// default all filenames to changelog.txt if none have been specified
			files = new String[templates.length];
			for (int i = 0; i < files.length; i++) {
				files[i] = "changelog.txt";
			}
		}
    clWriter.build(jiraApi.getVersionInfoList(), files, templates, ending);
    
    Logger.log("Done - Success!");
    System.exit(0);
	}
}