package net.foxopen.jira.changelog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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
 */
public class JiraAPI
{
  private final String username_, password_;
  private final URI jiraServerURI_;
  private LinkedList<VersionInfo> versionList_;
  
  
  /**
   * JiraAPI Constructor that accepts the basicinformation require to
   * communicate with a JIRA instance. 
   * 
   * @param username The username used to authenticate with JIRA.
   * @param password The password used to authenticate with JIRA.
   * @param URL The URL pointing to the JIRA instance.
   */
  public JiraAPI(String username, String password, String URL)
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
  }
  
  
  /**
   * @param projectKey The key used for the project in JIRA.
   * @param versionLabel The version label from JIRA (belonging to the project specified with projectKey
   * that you are currently building).
   */
  public void fetchVersionDetails(String projectKey, String versionLabel)
  { 
    try {
      final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
      final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerURI_, username_, password_);
      final NullProgressMonitor pm = new NullProgressMonitor();
      Project proj = restClient.getProjectClient().getProject(projectKey, pm);
      
      Version buildVersion = null;
      for (Version v : proj.getVersions()) {
        if (v.getName().equals(versionLabel)) {
          buildVersion = v;
          VersionInputBuilder vib = new VersionInputBuilder(projectKey, buildVersion);
          vib.setReleased(true);
          vib.setReleaseDate(new DateTime());
          //restClient.getVersionRestClient().updateVersion(v.getSelf(), vib.build(), pm);
        }
      }
      
      if (buildVersion == null) {
        throw new RuntimeException("Could not find a version in JIRA matching the version label argument: \"" + versionLabel + "\".");
      }
      
      versionList_ = new LinkedList<VersionInfo>();
      VersionInfoCache cache = new VersionInfoCache(projectKey, "U:\\object_cache");
      for (Version v : proj.getVersions()) {
        if (v.getReleaseDate() != null && v.isReleased()) { 
          if (v.getReleaseDate().isBefore(buildVersion.getReleaseDate()) || v.getReleaseDate().isEqual(buildVersion.getReleaseDate())) {
            
            // Attempt to get the changelog from the cache. If it can't be found or were trying
            // to generate a changelog for the current version then build/rebuild and cache.
            VersionInfo vi = cache.getCached(v.getName());
            if (vi == null || v.getName().equals(versionLabel)) {
              List<String> issueList = new ArrayList<String>();
              SearchResult sr = restClient.getSearchClient().searchJql("project = '" + projectKey + "' and fixVersion = '" + v.getName() + "'", pm);
              
              for (BasicIssue bi : sr.getIssues()) {
                Issue i = restClient.getIssueClient().getIssue(bi.getKey(), pm);
                
                String changelogDescription;
                try {
                  changelogDescription = i.getFieldByName("Changelog Description").getValue().toString();
                } catch (NullPointerException npe) {
                  // Changelog Description doesn't exist as a field for this issue so just default to the summary. 
                  changelogDescription = i.getSummary();
                }
                
                issueList.add("[" + i.getKey() + "] " + changelogDescription);
              }
              
              vi = new VersionInfo(v.getName(), v.getDescription(), v.getReleaseDate().toDate(), issueList);
              cache.cache(vi);
              versionList_.add(vi);
            } else {
              versionList_.add(vi);
            }
          }
        }
      }
  
      // Sort the version by release date.
      Collections.sort(versionList_, new DateComparator());
    } catch (RestClientException uh) {
      System.err.println("The JIRA instance is not reacable using the URL - \"" + jiraServerURI_ + "\".");
      System.exit(1);
    }
  }
  
  public LinkedList<VersionInfo> getVersionInfoList()
  {
    return versionList_;
  }
}

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