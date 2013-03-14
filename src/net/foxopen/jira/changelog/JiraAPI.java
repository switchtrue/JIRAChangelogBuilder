package net.foxopen.jira.changelog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.domain.input.VersionInputBuilder;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

/**
 * @author leonmi
 *
 * JiraAPI acts as a single point to communicate with JIRA and extract the version information
 * for use with the ChangelogBuilder. 
 */
public class JiraAPI
{
  private final String username_, password_;
  private final URI jiraServerURI_;
  private boolean setReleasedInJira_;
  private HashSet<String> issueTypeIgnoreSet_;
  private LinkedList<VersionInfo> versionList_;
  private VersionInfoCache cache_;
  
  /**
   * JiraAPI Constructor that accepts the basicinformation require to
   * communicate with a JIRA instance. 
   * 
   * @param username The username used to authenticate with JIRA.
   * @param password The password used to authenticate with JIRA.
   * @param URL The URL pointing to the JIRA instance.
   */
  public JiraAPI(String username, String password, String URL, boolean setReleasedInJira, String ignoreIssueTypeCsv)
  {
    username_ = username;
    password_ = password;
    URI tempURI = null;
    try {
      tempURI = new URI(URL);
    } catch (URISyntaxException e) {
      System.err.println("The JIRA URL supplied - \"" + URL + "\" is not a valid URI.");
      System.exit(1);
    } finally {
      jiraServerURI_ = tempURI;
    }
    setReleasedInJira_ = setReleasedInJira;
    issueTypeIgnoreSet_ = new HashSet<String>();
    issueTypeIgnoreSet_.addAll(Arrays.asList(ignoreIssueTypeCsv.split(",")));
  }
  
  public void setVersionInfoCache(VersionInfoCache cache) 
  {
    cache_ = cache;
  }
  
  
  /**
   * Communicate with JIRA to find all versions prior to the version you are currently building
   * for each version found get a list of issues fixed in that version from the serialized java object
   * cache on disk, or pull the list of issues from JIRA. Finally add all these versions to a LinkedList
   * and sort by date descending.
   * 
   * @param projectKey The key used for the project in JIRA.
   * @param versionLabel The version label from JIRA (belonging to the project specified with projectKey
   * that you are currently building).
   */
  public void fetchVersionDetails(String projectKey, String versionLabel)
  { 
    try {
      // Create the initial JIRA connection.
      final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
      final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerURI_, username_, password_);
      final NullProgressMonitor pm = new NullProgressMonitor();

      // Get an instance of the JIRA Project
      Project proj = restClient.getProjectClient().getProject(projectKey, pm);
      
      // Get a list of versions for this project and identify the one were currently trying to build.
      Version buildVersion = null;
      for (Version v : proj.getVersions()) {
        if (v.getName().equals(versionLabel)) {
          buildVersion = v;
        }
      }
      
      if (buildVersion == null) {
        System.err.println("Could not find a version in JIRA matching the version label argument: \"" + versionLabel + "\".");
        System.exit(1);
      }
      
      versionList_ = new LinkedList<VersionInfo>();
      
      // For each version determine if it was released prior to the current build. If so get a list of issues fixed in it and 
      // and add it to a LinkedList. If the version has been previously cached the data will be pulled from the cache.
      // the version being currently built will never be pulled from the cache.
      for (Version v : proj.getVersions()) {
        if ((v.getReleaseDate() != null && v.isReleased()) || v.getName().equals(versionLabel)) { 
          DateTime versionReleaseDate = v.getReleaseDate();
          if (versionReleaseDate == null) {
            versionReleaseDate = new DateTime();
          }
          if (v.getName().equals(versionLabel) || versionReleaseDate.isBefore(buildVersion.getReleaseDate()) || versionReleaseDate.isEqual(buildVersion.getReleaseDate())) {
            
            // Attempt to get the changelog from the cache. If it can't be found or were trying
            // to generate a changelog for the current version then build/rebuild and cache.
            VersionInfo vi = null;
            if (cache_ != null) {
              vi = cache_.getCached(v.getName());
            }
            if (vi == null || v.getName().equals(versionLabel)) {
              LinkedList<String> issueList = new LinkedList<String>();
              SearchResult sr = restClient.getSearchClient().searchJql("project = '" + projectKey + "' and fixVersion = '" + v.getName() + "'", pm);
              
              for (BasicIssue bi : sr.getIssues()) {
                Issue i = restClient.getIssueClient().getIssue(bi.getKey(), pm);
                
                // Only add this issue if its not in the ignore list.
                if (!issueTypeIgnoreSet_.contains(i.getIssueType().getName())) {
                  String changelogDescription;
                  try {
                    changelogDescription = i.getFieldByName("Changelog Description").getValue().toString();
                  } catch (NullPointerException npe) {
                    // Changelog Description doesn't exist as a field for this issue so just default to the summary. 
                    changelogDescription = i.getSummary();
                  }
                  
                  issueList.add("[" + i.getKey() + "] " + changelogDescription);
                }
              }
              
              vi = new VersionInfo(v.getName(), v.getDescription(), versionReleaseDate.toDate(), issueList);
              if (cache_ != null) {
                cache_.cache(vi);
              }
              versionList_.add(vi);
            } else {
              versionList_.add(vi);
            }
          }
        }
      }
  
      // Sort the version by release date descending.
      Collections.sort(versionList_, new DateComparator());
      
    } catch (RestClientException uh) {
      // Awful error handling block becase all errors seem to be of exception type RestClientException.
      
      if (uh.getMessage().startsWith("No project could be found with key")) {
        System.err.println("A project with the key \"" + projectKey + "\" could not be found in the JIRA instance at \"" + jiraServerURI_ + "\".");
        System.exit(1);
      }
      
      if (uh.getMessage().startsWith("com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException:")) {
        System.err.println("A JIRA instance could not be reached at \"" + jiraServerURI_ + "\".");
        System.exit(1);
      }
      
      uh.printStackTrace();
      System.exit(1);
    }
  }
  
  /**
   * @return LinkedList of VersionInfo instances giving details about each JIRA version
   * to be included in the change log and their issues. Ordered descending by release date.
   */
  public LinkedList<VersionInfo> getVersionInfoList()
  {
    return versionList_;
  }
  
  public void releaseVersion(String projectKey, String versionLabel)
  {
    if (setReleasedInJira_) {
      try {
        // Create the initial JIRA connection.
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerURI_, username_, password_);
        final NullProgressMonitor pm = new NullProgressMonitor();
    
        // Get an instance of the JIRA Project
        Project proj = restClient.getProjectClient().getProject(projectKey, pm);
        
        // Get a list of versions for this project and identify the one were currently trying to build.
        Version buildVersion = null;
        for (Version v : proj.getVersions()) {
          if (v.getName().equals(versionLabel)) {
            buildVersion = v;
            VersionInputBuilder vib = new VersionInputBuilder(projectKey, buildVersion);
            vib.setReleased(true);
            vib.setReleaseDate(new DateTime());
            restClient.getVersionRestClient().updateVersion(v.getSelf(), vib.build(), pm);
          }
        }
      } catch (RestClientException uh) {
        // Awful error handling block becase all errors seem to be of exception type RestClientException.
        
        if (uh.getMessage().startsWith("No project could be found with key")) {
          System.err.println("A project with the key \"" + projectKey + "\" could not be found in the JIRA instance at \"" + jiraServerURI_ + "\".");
          System.exit(1);
        }
        
        if (uh.getMessage().startsWith("com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException:")) {
          System.err.println("A JIRA instance could not be reached at \"" + jiraServerURI_ + "\".");
          System.exit(1);
        }
        
        uh.printStackTrace();
        System.exit(1);
      }
    }
  }
}

/**
 * @author leonmi
 *
 * Simple comparator that can be used to order by Date objects descending.
 */
class DateComparator implements Comparator<VersionInfo>
{
  public int compare(VersionInfo a, VersionInfo b)
  {
    if (a.getReleaseDate().after(b.getReleaseDate())) {
      return -1;
    } else if (a.getReleaseDate().before(b.getReleaseDate())) {
      return 1;
    } else {
      return 0;
    }
  }
}