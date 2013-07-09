package net.foxopen.jira.changelog;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonmi
 *
 * Basic class to store metadata about a JIRA version including label, description
 * release date and issues that were fixed in this version.
 * 
 * This class is serializable and will be serialized to cache the results.
 */
public class VersionInfo implements Serializable
{
  private static final long serialVersionUID = 4317403361667148998L;
 
  private String name_;
  private String description_;
  private Date releaseDate_;
  private LinkedList<String> issueList_;
  
  /**
   * VersionInfo constructor accepting all require information as parameters. This also ensures that the list
   * of issues fixed in this version are sorted.
   * 
   * @param name of the JIRA project version.
   * @param description of the JIRA project version. 
   * @param releaseDate of the JIRA project version.
   * @param issueList issues fixed in the JIRA project version.
   */
  public VersionInfo(String name, String description, Date releaseDate, LinkedList<String> issueList)
  {
    name_ = name;
    description_ = description;
    releaseDate_ = releaseDate;
    issueList_ = issueList; 
    Collections.sort(issueList_);
  }
  
  /**
   * @return the name of this JIRA version.
   */
  public String getName()
  {
    return name_;
  }
  
  /**
   * @return the description of this JIRA version.
   */
  public String getDescription()
  {
    return description_;
  }
  
  /**
   * @return the release date of this JIRA version.
   */
  public Date getReleaseDate()
  {
    return releaseDate_;
  }
  
  /**
   * @return the list of issues fixed in this JIRA version.
   */
  public List<String> getIssueList()
  {
    return issueList_;
  }
  
  public boolean hasIssues()
  {
    return !issueList_.isEmpty();
  }
}
