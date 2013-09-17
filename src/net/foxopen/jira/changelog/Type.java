/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.foxopen.jira.changelog;

import java.util.LinkedList;
import java.io.Serializable;

/**
 * Defines an issue category (bug, task, support ticket, etc.)
 *
 * @author apigram
 * @version 1.03.00
 */
public class Type implements Serializable {
  private static final long serialVersionUID = 4317423361667148998L;
  String name;
  LinkedList<Change> issues;

  /**
   * Default constructor
   *
   * @param name The name of the type
   */
  public Type(String name) {
    this.name = name;
    issues = new LinkedList<Change>();
  }
}
