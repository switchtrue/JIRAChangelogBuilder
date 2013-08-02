package net.foxopen.jira.changelog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Basic class to store metadata about a JIRA version including label, description
 * release date and issues that were fixed in this version.
 * 
 * This class is serializable and will be serialized to cache the results.
 * @author leonmi
 *
 */
public class VersionInfo implements Serializable
{
  private static final long serialVersionUID = 4317403361667148998L;
	private final String LS = System.getProperty("line.separator");
 
  private String name;
  private String description;
  private Date releaseDate;
	private String dateText;
  private LinkedList<Change> issues;
	private List<Type> typeList; // stores the issues by type
  
  /**
   * VersionInfo constructor accepting all require information as parameters. This also ensures that the list
   * of issues fixed in this version are sorted.
   * 
   * @param name of the JIRA project version.
   * @param description of the JIRA project version. 
   * @param releaseDate of the JIRA project version.
   * @param issueList issues fixed in the JIRA project version.
   */
  public VersionInfo(String name, String description, Date releaseDate, LinkedList<Change> issueList)
  {
    this.name = name;
    this.description = description;
    this.releaseDate = releaseDate;
    this.issues = issueList;
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd-MM-yyyy");
		dateText = sdf.format(releaseDate);
    Collections.sort(this.issues, new ChangeComparator());
		
		// initialise the hashmap and create the sub-lists. 
		// The key is purely for lookup when inserting issues. It is not used for
		// templating at all.
		typeList = new LinkedList<Type>();
		
		typeList.add(new Type("Bugs"));
		typeList.add(new Type("Improvements"));
		typeList.add(new Type("Change Requests"));
		typeList.add(new Type("Tasks"));
		typeList.add(new Type("Support Tickets"));
		typeList.add(new Type("Data Issues"));
		typeList.add(new Type("Epics"));
		typeList.add(new Type("New Features"));
		
		for (Change c : this.issues) {
			if (c.getType().equals("Bug")) {
				typeList.get(0).issues.add(c);
			}
			else if (c.getType().equals("Improvement")) {
				typeList.get(1).issues.add(c);
			}
			else if (c.getType().equals("Change Request")) {
				typeList.get(2).issues.add(c);
			}
			else if (c.getType().equals("Task")) {
				typeList.get(3).issues.add(c);
			}
			else if (c.getType().equals("Support Ticket")) {
				typeList.get(4).issues.add(c);
			}
			else if (c.getType().equals("Data Issue")) {
				typeList.get(5).issues.add(c);
			}
			else if (c.getType().equals("Epic")) {
				typeList.get(6).issues.add(c);
			}
			else if (c.getType().equals("New Feature")) {
				typeList.get(7).issues.add(c);
			}
		}
		
		// remove all empty types from the list (to avoid displaying unnecessary category headings
		for (int i = 0; i < typeList.size(); i++) {
			if (typeList.get(i).issues.size() == 0) {
				typeList.remove(i);
				i = -1; // reset the counter to count from the beginning (as the list has now shrunk)
			}
		}
  }
	
	/**
	 * Gets the list of types used for the module changelog. Used implicitly by mustache
	 * @return The list of types
	 */
	List<Type> getTypeList() {
		return typeList;
	}
  
  /**
	 * Gets the name (version number) of the current version.
   * @return the name of this JIRA version.
   */
  public String getName()
  {
    return name;
  }
  
  /**
	 * Gets the description (if any) of the current version.
   * @return the description of this JIRA version.
   */
  public String getDescription()
  {
		// if no description, send a blank string. Otherwise, send the description with a leading line break
		if (description == null) {
			return "";
		} else {
			return LS + description;
		}
  }
  
  /**
	 * Gets the release date of the current version.
   * @return the release date of this JIRA version.
   */
  public Date getReleaseDate()
  {
    return releaseDate;
  }
	
	/**
	 * Gets the release date as a formatted string
	 * @return The release date formatted as dd-MM-yyyy
	 */
	public String getDateText()
	{
		return dateText;
	}
  
  /**
	 * Gets the list of issues for the current version.
   * @return the list of issues fixed in this JIRA version.
   */
  List<Change> getIssues()
  {
    return issues;
  }
  
	/**
	 * Determines whether the current version has issues associated with it.
	 * @return True if this version has issues listed, otherwise false.
	 */
  public boolean hasIssues()
  {
    return !issues.isEmpty();
  }
}
