package com.switchtrue.jira.changelog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

/**
 * JiraAPI acts as a single point to communicate with JIRA and extract the
 * version information for use with the ChangelogBuilder.
 *
 * @author mleonard87
 *
 */
public class JiraAPI {

  private final String username_, password_;
  private final URI jiraServerURI_;
  private String jql_;
  private String descriptionField_;
  private LinkedList<VersionInfo> versionList_;
  private VersionInfoCache cache_;
  private String fixVersionRestrictMode_;
  private String fixVersionRestrictTerm_;
  private Version buildVersion = null;

  /**
   * JiraAPI Constructor that accepts the basic information require to
   * communicate with a JIRA instance.
   *
   * @param username The username used to authenticate with JIRA.
   * @param password The password used to authenticate with JIRA.
   * @param URL The URL pointing to the JIRA instance.
   * @param descriptionField The name of the field in JIRA to use as the
   * changelog description.
   */
  public JiraAPI(String username, String password, String URL, String jql, String descriptionField, String fixVersionRestrictMode, String fixVersionRestrictTerm) {
    username_ = username;
    password_ = password;

    if (jql.equals("")) {
      jql_ = "";
    } else {
      jql_ = " and (" + jql + ")";
    }

    descriptionField_ = descriptionField;
    fixVersionRestrictMode_ = fixVersionRestrictMode;
    fixVersionRestrictTerm_ = fixVersionRestrictTerm;

    URI tempURI = null;
    try {
      tempURI = new URI(URL);
    } catch (URISyntaxException e) {
      Logger.err("The JIRA URL supplied - \"" + URL + "\" is not a valid URI.");
    } finally {
      jiraServerURI_ = tempURI;
    }
  }

  /**
   * Sets the version info cache
   *
   * @param cache The version info cache.
   */
  public void setVersionInfoCache(VersionInfoCache cache) {
    cache_ = cache;
  }

  private boolean doesVersionNameMatchFilter(String versionName) {
    if (fixVersionRestrictMode_ == null) {
      return true;
    }
    
    if (fixVersionRestrictMode_ == Changelog.FIX_VERSION_RESTICT_MODE_STARTS_WITH) {
      if (versionName.startsWith(fixVersionRestrictTerm_)) {
        Logger.log("Version '" + versionName + "' starts with '" + fixVersionRestrictTerm_ + "'.");
        return true;
      } else {
        Logger.log("Version '" + versionName + "' does not start with '" + fixVersionRestrictTerm_ + "'. Omitting from changelog.");
        return false;
      }
    }
    
    if (fixVersionRestrictMode_ == Changelog.FIX_VERSION_RESTICT_MODE_LESS_THAN_OR_EQUAL) {
      int compare = versionName.compareTo(fixVersionRestrictTerm_);
      if (compare <= 0){
        Logger.log("Version '" + versionName + "' is less than or equal to '" + fixVersionRestrictTerm_ + "'.");
        return true;
      } else {
        Logger.log("Version '" + versionName + "' is not less than or equal to '" + fixVersionRestrictTerm_ + "'. Omitting from changelog.");
        return false;
      }
    }
    
    return false;
  }
  
  private boolean isVersionBeforeOrEqualToBuildReleaseDate(DateTime versionReleaseDate) {
    if (versionReleaseDate.isBefore(buildVersion.getReleaseDate()) || versionReleaseDate.isEqual(buildVersion.getReleaseDate())) {
      return true;
    }
    
    return false;
  }
  
  /**
   * Communicate with JIRA to find all versions prior to the version you are
   * currently building for each version found get a list of issues fixed in
   * that version from the serialized java object cache on disk, or pull the
   * list of issues from JIRA. Finally add all these versions to a LinkedList
   * and sort by date descending.
   *
   * @param projectKey The key used for the project in JIRA.
   * @param versionLabel The version label from JIRA (belonging to the project
   * specified with projectKey that you are currently building).
   */
  public void fetchVersionDetails(String projectKey, String versionLabel) {
    try {
      // Create the initial JIRA connection.
      Logger.log("Establishing JIRA API connection for generating changelog to " + jiraServerURI_ + ".");
      JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
      final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerURI_, username_, password_);

      // Get an instance of the JIRA Project
      Logger.log("Obtaining project information via JIRA API.");
      ProjectRestClient projectClient = restClient.getProjectClient();
      Promise<Project> promise = projectClient.getProject(projectKey);
      Project proj = promise.claim();

      // Get a list of versions for this project and identify the one were
      // currently trying to build.
      Logger.log("Determining if the version '" + versionLabel + "' exists in JIRA.");
      for (Version v : proj.getVersions()) {
        if (v.getName().equals(versionLabel)) {
          buildVersion = v;
        }
      }

      if (buildVersion == null) {
        Logger.err("Could not find a version in JIRA matching the version label argument: \"" + versionLabel + "\".");
        System.exit(1);
      }

      Logger.log("Version '" + versionLabel + "' found in JIRA.");

      versionList_ = new LinkedList<VersionInfo>();

      // For each version determine if it was released prior to the current
      // build. If so get a list of issues fixed in it and
      // and add it to a LinkedList. If the version has been previously cached
      // the data will be pulled from the cache.
      // the version being currently built will never be pulled from the cache.
      for (Version v : proj.getVersions()) {
        if ((v.getReleaseDate() != null && v.isReleased()) || v.getName().equals(versionLabel)) {
          DateTime versionReleaseDate = v.getReleaseDate();
          if (versionReleaseDate == null) {
            versionReleaseDate = new DateTime();
          }
          if ( (v.getName().equals(versionLabel) || isVersionBeforeOrEqualToBuildReleaseDate(versionReleaseDate)) && doesVersionNameMatchFilter(v.getName()) ) {
            Logger.log("Adding '" + v.getName() + "' to changelog.");
            // Attempt to get the changelog from the cache. If it can't be found
            // or were trying to generate a changelog for the current version then
            // build/rebuild and cache.
            VersionInfo vi = null;
            if (cache_ != null && !v.getName().equals(versionLabel)) {
              vi = cache_.getCached(v.getName());
            }
            if (vi == null) {
              LinkedList<Change> issueList = new LinkedList<Change>();
              Logger.log("Obtaining issues related to '" + v.getName() + "' via JIRA API.");
              SearchResult sr = null;
              try {
              Promise<SearchResult> searchJql = restClient.getSearchClient().searchJql("project = '" + projectKey + "' and fixVersion = '" + v.getName() + "'" + jql_);
              sr = searchJql.claim();

                for (BasicIssue bi : sr.getIssues()) {
                  Logger.log("Obtaining further issue details for issue '" + bi.getKey() + "' via JIRA API.");
                  Promise<Issue> issue = restClient.getIssueClient().getIssue(bi.getKey());
                  Issue i = issue.claim();

                  // Add this issue
                  String changelogDescription;
                  String type = null;
                  try {
                    changelogDescription = i.getFieldByName(descriptionField_).getValue().toString();
                  } catch (NullPointerException npe) {
                    // Changelog Description doesn't exist as a field for this
                    // issue so just default to the summary.
                    changelogDescription = i.getSummary();
                  }
                  type = i.getIssueType().getName();
                  issueList.add(new Change(i.getKey(), changelogDescription, type));
                }
              } catch (RestClientException jqlErr) {
                Logger.log("The additional JQL string supplied was either invalid or returned no results. Ignoring additional JQL.");
                Promise<SearchResult> searchJql = restClient.getSearchClient().searchJql("project = '" + projectKey + "' and fixVersion = '" + v.getName() + "'" + jql_);
                sr = searchJql.claim();
                for (BasicIssue bi : sr.getIssues()) {
                  Logger.log("Obtaining further issue details for issue '" + bi.getKey() + "' via JIRA API.");
                  Promise<Issue> issue = restClient.getIssueClient().getIssue(bi.getKey());
                  Issue i = issue.claim(); 

                  // Add this issue
                  String changelogDescription = null;
                  String type = null;
                  try {
                    changelogDescription = i.getFieldByName(descriptionField_).getValue().toString();
                  } catch (NullPointerException npe) {
                    // Changelog Description doesn't exist as a field for this
                    // issue so just default to the summary.
                    changelogDescription = i.getSummary();
                  }
                  type = i.getIssueType().getName();
                  issueList.add(new Change(i.getKey(), changelogDescription, type));
                }
              }
              vi = new VersionInfo(v.getName(), v.getDescription(), versionReleaseDate.toDate(), issueList);
              if (cache_ != null) {
                cache_.cache(vi);
              }
            }
            versionList_.add(vi);
          }
        }
      }

      // Sort the version by release date descending.
      Logger.log("Sorting versions by release date descending.");
      Collections.sort(versionList_, new DateComparator());

    } catch (RestClientException uh) {
      // Awful error handling block becase all errors seem to be of exception
      // type RestClientException.

      if (uh.getMessage().startsWith("No project could be found with key")) {
        Logger.err("A project with the key \"" + projectKey + "\" could not be found in the JIRA instance at \"" + jiraServerURI_ + "\".");
      }

      if (uh.getMessage().startsWith("com.sun.jersey.api.client.ClientHandlerException: java.net.UnknownHostException:")) {
        Logger.err("A JIRA instance could not be reached at \"" + jiraServerURI_ + "\".");
      }

      uh.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Gets the list of JIRA versions to be included in the changelog, as well as
   * their issues.
   *
   * @return LinkedList of VersionInfo instances giving details about each JIRA
   * version to be included in the change log and their issues. Ordered
   * descending by release date.
   */
  public LinkedList<VersionInfo> getVersionInfoList() {
    return versionList_;
  }
}

/**
 * Simple comparator that can be used to order by Date objects descending.
 *
 * @author leonmi
 *
 */
class DateComparator implements Comparator<VersionInfo> {

  /**
   * Compare the value of two dates. Used to sort issues and versions by date
   *
   * @param a A date to compare with
   * @param b Another date to compare
   * @return -1 if <param>a</param> &gt; <param>b</param>, 1 if <param>a</param>
   * &lt; <param>b</param>, otherwise 0
   */
  public int compare(VersionInfo a, VersionInfo b) {
    if (a.getReleaseDate().after(b.getReleaseDate())) {
      return -1;
    } else if (a.getReleaseDate().before(b.getReleaseDate())) {
      return 1;
    } else {
      return -(a.getName().compareTo(b.getName()));
    }
  }
}
