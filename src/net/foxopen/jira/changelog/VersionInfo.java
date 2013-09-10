package net.foxopen.jira.changelog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

/**
 * Basic class to store metadata about a JIRA version including label,
 * description release date and issues that were fixed in this version.
 *
 * This class is serializable and will be serialized to cache the results.
 *
 * @author mleonard87
 *
 */
public class VersionInfo implements Serializable {

  private static final long serialVersionUID = 4317403361667148998L;
  private final String LS = System.getProperty("line.separator");
  private String name;
  private String description;
  private Date releaseDate;
  private String dateText;
  private LinkedList<Change> issues;
  private HashMap<String, Type> issueTypes; // stores the issues by type

  /**
   * VersionInfo constructor accepting all require information as parameters.
   * This also ensures that the list of issues fixed in this version are sorted.
   *
   * @param name of the JIRA project version.
   * @param description of the JIRA project version.
   * @param releaseDate of the JIRA project version.
   * @param issueList issues fixed in the JIRA project version.
   */
  public VersionInfo(String name, String description, Date releaseDate, LinkedList<Change> issueList) {
    this.name = name;
    this.description = description;
    this.releaseDate = releaseDate;
    this.issues = issueList;

    SimpleDateFormat sdf = new SimpleDateFormat();
    sdf.applyPattern("dd-MM-yyyy");
    dateText = sdf.format(releaseDate);
    Collections.sort(this.issues, new ChangeComparator());

    // Initialise the hashmap and create the sub-lists.
    // The key is purely for lookup when inserting issues. It is not used for templating at all.
    issueTypes = new HashMap<String, Type>();

    for (Change c : this.issues) {
      Type t = issueTypes.get(c.getType());
      if (t == null) {
        // add the type to the list, then add the issue to that type.
        issueTypes.put(c.getType(), new Type(c.getType()));
        issueTypes.get(c.getType()).issues.add(c);
      } else {
        // type already exists so add the issue to that type.
        t.issues.add(c);
      }
    }
  }

  /**
   * Gets the list of types used for the module changelog. Used implicitly by
   * mustache
   *
   * @return The list of types
   */
  public Collection<Type> getIssueTypes() {
    return issueTypes.values();
  }

  /**
   * Gets the name (version number) of the current version.
   *
   * @return the name of this JIRA version.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description (if any) of the current version.
   *
   * @return the description of this JIRA version.
   */
  public String getDescription() {
    // if no description, send a blank string. Otherwise, send the description
    // with a leading line break
    if (description == null) {
      return "";
    } else {
      return LS + description;
    }
  }

  /**
   * Gets the release date of the current version.
   *
   * @return the release date of this JIRA version.
   */
  public Date getReleaseDate() {
    return releaseDate;
  }

  /**
   * Gets the release date as a formatted string
   *
   * @return The release date formatted as dd-MM-yyyy
   */
  public String getReleaseDateText() {
    return dateText;
  }

  /**
   * Gets the list of issues for the current version.
   *
   * @return the list of issues fixed in this JIRA version.
   */
  List<Change> getIssues() {
    return issues;
  }

  /**
   * Determines whether the current version has issues associated with it.
   *
   * @return True if this version has issues listed, otherwise false.
   */
  public boolean hasIssues() {
    return !issues.isEmpty();
  }
}
