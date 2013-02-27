package net.foxopen.jira.changelog;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VersionInfo implements Serializable
{
  private static final long serialVersionUID = 4317403361667148998L;
 
  private String label_;
  private String description_;
  private Date releaseDate_;
  private List<String> issueList_;
  
  public VersionInfo(String label, String description, Date releaseDate, List<String> issueList)
  {
    label_ = label;
    description_ = description;
    releaseDate_ = releaseDate;
    issueList_ = issueList; 
  }
  
  public String getLabel()
  {
    return label_;
  }
  
  public String getDescription()
  {
    return description_;
  }
  
  public Date getReleaseDate()
  {
    return releaseDate_;
  }
  
  public List<String> getIssueList()
  {
    return issueList_;
  }
}
